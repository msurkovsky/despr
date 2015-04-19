
package cz.vsb.cs.sur096.despr.window;

import cz.vsb.cs.sur096.despr.Despr;
import cz.vsb.cs.sur096.despr.events.MessageEvent;
import cz.vsb.cs.sur096.despr.events.MessageListener;
import cz.vsb.cs.sur096.despr.model.IOperationModel;
import cz.vsb.cs.sur096.despr.model.operation.IOperation;
import cz.vsb.cs.sur096.despr.structures.LocalizeMessages;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParametersTable;
import cz.vsb.cs.sur096.despr.view.inputparameters.ParametersTableModel;
import cz.vsb.cs.sur096.despr.view.operation.Displayable;
import cz.vsb.cs.sur096.despr.window.actions.ExecuteOperationAction;
import java.awt.*;
import javax.swing.*;

/**
 * Komponenta zobrazující náhled na výsledek zobrazitelné operace, tj.
 * operace implementující rozhraní {@code Displayable}.
 *
 * @author Martin Šurkovský, sur096, 
 * <a href="mailto:martin.surkovsky@gmail.com">martin.surkovsky at gmail.com</a>
 */
public class ThumbnailPanel extends JSplitPane implements MessageListener {
 
    private transient LocalizeMessages messages;
    private IOperationModel op;
    private JLabel lblImg;
    private ExecuteOperationAction executeOperation;

    /**
     * Iniciuje panel s modelem operace pro který bude náhled generován.
     * @param op operace která poskytne náhled ke zobrazení.
     */
    public ThumbnailPanel(IOperationModel op) throws NullPointerException {
        super(JSplitPane.VERTICAL_SPLIT);
        messages = Despr.loadLocalizeMessages(getClass(), null, false);
        this.op = op;
        setPreferredSize(new Dimension(400,400));
        setDividerLocation(250);
        setContinuousLayout(true);
        init();
    }
    
    /**
     * Iniciace obsahu panel. Je rozdělen na horní a spodní část. Horní 
	 * část obsahuje náhled na poskytnutý obrázek a spodní vstupní
	 * parametry operace které ovlivňují výsledek.
     */
    private void init() 
            throws NullPointerException {
        lblImg = new JLabel();
        
        if (op.getOperation() instanceof Displayable) {
            Image img = ((Displayable) op.getOperation()).getThumbnail();
            if (img == null) {
                String message = messages.getString("exception.null_image", 
                        "Image must not be NULL!");
                throw new NullPointerException(message);
            }
            ImageIcon imageIcon = new ImageIcon(img);
            lblImg.setIcon(imageIcon);
            JScrollPane imgScrPane = new JScrollPane(lblImg);
            imgScrPane.getViewport().setBackground(Color.LIGHT_GRAY);
            add(imgScrPane);
        }
        
        JPanel pnlBottom = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 0.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 3, 0, 3);
        
        executeOperation = new ExecuteOperationAction(op);
        executeOperation.putValue(AbstractAction.NAME, null);
        executeOperation.addMessageListener(this);
        JButton btnExecute = new JButton(executeOperation);

        pnlBottom.add(btnExecute, gbc);
        
        ParametersTableModel ptm = new ParametersTableModel();
        ptm.setInputParameters(op.getInputParameters());
        ParametersTable pt = new ParametersTable(ptm);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 0, 0, 0);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pnlBottom.add(new JScrollPane(pt), gbc);
        
        add(pnlBottom);
    }

    /**
     * Reaguje na poslanou zprávu o ukončení zpracování operace.
     * @param event událost která poslala zprávu.
     */
    @Override
    public void catchMessage(MessageEvent event) {
        String msg = event.getMessage();
        if (msg.equals("done")) {
            IOperation operation = op.getOperation();
            if (operation instanceof Displayable) {
                lblImg.setIcon(new ImageIcon(((Displayable) operation).getThumbnail()));
            }
        }
    }
}
