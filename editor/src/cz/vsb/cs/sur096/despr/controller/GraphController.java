
package cz.vsb.cs.sur096.despr.controller;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.collections.IParameters;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgeException;
import cz.vsb.cs.sur096.despr.exceptions.IncorrectEdgesException;
import cz.vsb.cs.sur096.despr.exceptions.ParameterUsedException;
import cz.vsb.cs.sur096.despr.model.*;
import cz.vsb.cs.sur096.despr.model.operation.parameter.EInputParameterType;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.window.DesprOut;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * Implementace rozhraní {@code IGraphControler}, která se stará o vyhodnocení
 * a běh grafu.
 *
 * @author Martin Šurkovský, sur096,
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 * @version 2012/02/28/08:45
 */
public class GraphController implements IGraphController {
 
	/** Seznam lokalizačních zpráv */
    private transient LocalizeMessages messages;
	/** Seznam posluchačů zajímajících se o změnu stavu zpracování grafu*/
    private transient List<ProgressChangeListener> progressChangeListeners;
    
	/** Mode grafu, který má být zpracován*/
    private IGraph model;

	/** Přiznak přerušení vyhodnocování graf*/
    private boolean stopExecute;
    
	/**
	 * Inicializuje vyhodnocení grafu.
	 */
    public GraphController(IGraph model) {
        this.model = model;
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        progressChangeListeners = new ArrayList<ProgressChangeListener>(1);
    }

    /**
     * Poskytne celkový počet iterací.
     * @return celkový počet iterací, zpracovávaných dat. Je určen tak,
	 * že se projdou všechny kořenové operace, zjistí se kolik má která
	 * dat na zpracování a vybere se nejmenší počet.
     */
    @Override
    public int getLengthOfExecute() {
        IRootOperationModel currentOp = null;
        try {
            int length = Integer.MAX_VALUE;
            List<IRootOperationModel> rootOperations = model.getRootOperations();
            for (IRootOperationModel rootOp : rootOperations) {
                currentOp = rootOp;
                int count = currentOp.getCountItems();
				// operace s poctem -1 maji definovan nekonecny iterator
                if (count > -1 && count < length) {
                    length = count;
                }
            }
            // pokud se nenasla operace s konecnym poctem prvku ke zpracovani
            // je vracen pocet prvku ke zpracovani nulovy.
			if (length == Integer.MAX_VALUE) {
                length = 0;
                List<IOperationModel> operations = model.getOperations();
                for (IOperationModel opModel : operations) {
                    if (opModel.getLevel() == 0 && 
                            !(opModel instanceof IRootOperationModel)) {
                        
                        length = 1;
                        break;
                    }
                }
            }
            return length;
        } catch (RuntimeException ex) { // muze vyhodit metoda getCountItems()
            if (currentOp != null) {
                currentOp.fireException(ex); 
            }
            
            // metoda getCout muze spolupracovat se vstupnimi paremetry
            // rotovske operace a tek je mozne ze vyhodi nejakou chybu
            stopExecute = true;
            Despr.showError(messages.getString("title.execution_error", "Execution error"), 
                    ex, Level.WARNING, true);
            return 0;
        }
    }
    
    /**
     * Provede kontrolu grafu. Kontroluje se pouze zda nejsou
	 * nepoužité vnější vstupní parametry.
     * @throws ParameterUsedException pokud existuje vstupní-vnější parametr,
	 * který není použit.
     * @throws IncorrectEdgesException pokud se v grafu nachází nekorektní hrany.
     */
    @Override
    public void verifyGraph() throws ParameterUsedException, IncorrectEdgesException {

        boolean existsFinalRootOperation = false;
        List<IRootOperationModel> rootOperations = model.getRootOperations();
        for (IRootOperationModel opModel : rootOperations) {
            if (opModel.getCountItems() > -1) {
                existsFinalRootOperation = true;
                break;
            }
        }
        
        if (!existsFinalRootOperation && !rootOperations.isEmpty()) {
            String message = messages.getString("excpetion.no_final_root_operation", 
                    "Does not exist any final root operation!");
            throw new RuntimeException(message);
        }
        
        boolean existsZeroLevelOperation = false;
        List<IOperationModel> operations = model.getOperations();
		for (IOperationModel op : operations) {

            if (!(op instanceof IRootOperationModel) && op.getLevel() == 0) {
                existsZeroLevelOperation = true;
            }
            
			// Jsou vsechny vstupni porty vyuzity.
			IParameters<IInputParameter> opInputPorts = op.getInputParameters();
			if (opInputPorts != null) {
				for (IInputParameter parameter : opInputPorts) {
					if (parameter.getType() == EInputParameterType.OUTER && !parameter.isUsed()) {
                        String message = String.format("%s '%s'!",
                                messages.getString("exception.unsed_parameter", 
                                "Input port must be use or switch to INNER type!"), 
                                parameter.toString());
                        throw new ParameterUsedException(message, parameter);
					}
				}
			}
		}
        
        if (rootOperations.isEmpty() && !existsZeroLevelOperation) {
            String message = messages.getString("exception.no_start_operation",
                    "Does not exist an operation which could activate the process.");
            throw new RuntimeException(message);
        }
        
        List<IEdge> edges = model.getEdges();
        List<IncorrectEdgeException> incorrectEdges = new ArrayList<IncorrectEdgeException>();
        for (IEdge edge : edges) {
            if (edge.isIncorrect()) {
                incorrectEdges.add(new IncorrectEdgeException(edge.getSource(), edge.getTarget()));
            }
        }
        
        if (!incorrectEdges.isEmpty()) {
            throw new IncorrectEdgesException(incorrectEdges);
        }
    }
  
    /**
     * Spustí samotné zpracování grafu.
     */
    @Override
    public void execute() throws Exception {
        
        long startTime = System.currentTimeMillis();
        try {
            verifyGraph();
            
            // vyresetovani korenovych operaci
            List<IRootOperationModel> rootOperations = model.getRootOperations();
            for (IRootOperationModel rootOp : rootOperations) {
                rootOp.resetIterator();
            }

            List<IOperationModel> operations = model.getOperations();
            final int numberLevels = getCountOfLevels(operations);

            if (numberLevels == 0) {
                if (!areRootOperationsReady(rootOperations)) {
                    throw new Exception(messages.getString("exception.root_ops_are_not_ready", 
                            "Not all root operations are input data!"));
                }
            }

            final ExecutorService executorService = new ScheduledThreadPoolExecutor(10);

            // rozrazeni operaci do hladin
            final List<IOperationModel> l[] = new List[numberLevels];

            for (int i = 0; i < numberLevels; i++) {
                // predpokladam nanejvis 10 operaci v ramci jedne hladiny.
                l[i] = new ArrayList<IOperationModel>(10);
            }

            // prirazeni operaci do jednotlivych hladin
            for (IOperationModel op : operations) {
                l[op.getLevel()].add(op);
            }

            stopExecute = false;
            int progress = 0;
            do {
                long cycleTimeStart = System.currentTimeMillis();
                // paralelni provedeni jednotlivych hladin.
                for (int i = 0; i < numberLevels; i++) {
                    try {
                        List<Future<Void>> futures = executorService.invokeAll(l[i]);
                        for (Future<Void> future : futures) {
                            future.get(); //pokud nastala chyba pri zpracovani
                                          // vyhodi vyjimku ExecutionException
                        }
                    } catch (ExecutionException ex) {
                        stopExecute = true;
                        Despr.showError(messages.getString("title.execution_error",
                                                           "Execution error"), 
                                ex, Level.WARNING, true);
                        
                    } catch (CancellationException ex) {
                        stopExecute = true;
                    } catch (InterruptedException ex) {
                        stopExecute = true;
                    }
                }

                if (stopExecute) {
                    break;
                }
                long cycleTimeEnd = System.currentTimeMillis();
                progress++;
                for (ProgressChangeListener pchl : progressChangeListeners) {
                    pchl.progressChange(progress, (cycleTimeEnd - cycleTimeStart));
                }
            } while (areRootOperationsReady(rootOperations) && !stopExecute);
            executorService.shutdown();
        } catch (ParameterUsedException ex) {
            Despr.showError(
                    messages.getString("title.unused_input_parameter_excp", "Unused input parameter"), 
                    ex, Level.WARNING, false);
        } catch (IncorrectEdgesException ex) {
            String title = messages.getString("title.incorrect_edge_excp", "Incorrect edges");
            Despr.showError(title, ex, Level.WARNING, false);
        } catch (RuntimeException ex) {
            String title = messages.getString("title.no_final_operation", "No final operation");
            Despr.showError(title, ex, Level.WARNING, false);
        } finally {
            long finishTime = System.currentTimeMillis();
            Date d = new Date(finishTime - startTime);
            String titleHours = messages.getString("title.hours", "hours");
            String titleMins = messages.getString("title.mins", "minutes");
            String titleSeconds = messages.getString("title.seconds", "seconds");
            DateFormat df = new SimpleDateFormat(
                    String.format("HH '%s', mm '%s' ss '%s'", 
                    titleHours, titleMins, titleSeconds));
            
            df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            String totalTime = String.format("%s: %s\n",
                    messages.getString("title.total_time", "Total time"),
                    df.format(d));
            
            if (Despr.getMode() == Despr.GRAPHICS_MODE) {
                DesprOut.printInfo(totalTime);
            } else {
                Despr.showConsoleInfo(totalTime);
            }
        }
    }
    
    /**
     * Přeruší zpracovávání grafu.
     */
    @Override
    public void stopExecuting() {
        stopExecute = true;
    }
    
    /**
     * Přidá posluchače na změnu úrovně celkového zpracování grafu.
     * @param l posluchač.
     */
    @Override
    public void addProgressChangeListener(ProgressChangeListener l) {
        progressChangeListeners.add(l);
    }

    /**
     * Smaže posluchače změny úrovně celkového zpracování grafu.
     * @param l posluchač.
     */
    @Override
    public void removeProgressChangeListener(ProgressChangeListener l) {
        progressChangeListeners.remove(l);
    }
    
    ////////////////////////////////////////////////////////////
    // Soukrome pomocne metody
    
    /**
	 * Metoda vracející celkový počet hladin, na který lze graf rozdělit.
	 * @return počet úrovni v grafu.
	 */
	private int getCountOfLevels(List<IOperationModel> operations) {

        int level = -1;
        for (IOperationModel op : operations) {
            if (op.getLevel() > level) {
                level = op.getLevel();
            }
        }
        return level + 1;  // plus jedna protoze se pocita od nuly!
	}
    
    /**
     * Zjistí zda mají všechny kořenové operace další data ke zpracování.
     * @param rootOperations seznam kořenových operací.
     * @return {@code true} pokud mají všechny operace další data
	 * ke zpracování, jinak {@code false}.
     */
    private boolean areRootOperationsReady(List<IRootOperationModel> rootOperations) {
        boolean ready = true;
        
        if (rootOperations.isEmpty()) {
            return false;
        }
        
        // maji vsechny rootovske operace data k dispozici.
        for (IRootOperationModel rootOp : rootOperations) {
            if(!rootOp.hasNext()) {
                ready = false;
                break;
            }
        }
        return ready;
    }
}