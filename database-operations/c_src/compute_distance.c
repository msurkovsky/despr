#ifdef STANDARD
#include <stdio.h>
#include <string.h>
#ifdef __WIN__
typedef unsigned __int64 ulonglong; /* Microsofts 64 bit types */
typedef __int64 longlong;
#else
typedef unsigned long long ulonglong;
typedef long long longlong;
#endif /*__WIN__*/
#else
#include <my_global.h>
#include <my_sys.h>
#endif /* __STANDARD__ */

#include <mysql.h>
#include <m_ctype.h>
#include <m_string.h>

#include <math.h>
#include <stdlib.h>

#define CBD 1
#define ED  2

#define ARGUMENTS_COUNT 5
#define VECTOR1 0
#define VECTOR2 1
#define VECTOR_SIZE 2
#define DELIMITER 3
#define METHOD 4

#define TOLERATION 4

// ktere z argumentu jsou konstantni? (1 - konstantni, 0 - ne)
static int constant_arg_compute_distance[ARGUMENTS_COUNT];
static int constant_arg_compare_vectors[ARGUMENTS_COUNT];

my_bool compute_distance_init(UDF_INIT *initid, UDF_ARGS *args, char *message);
void compute_distance_deinit(UDF_INIT *initid);
double compute_distance(UDF_INIT *initid, UDF_ARGS *args, char *is_null, char *error);

my_bool compare_vectors_init(UDF_INIT *initid, UDF_ARGS *args, char *message);
void compare_vectors_deinit(UDF_INIT *initid);
long long compare_vectors(UDF_INIT * initid, UDF_ARGS *args, char *is_null, char *error);

void vectorToString(double *, double *, long long, char *);
void clear_char_array(char *);
void clear_char_array2(char *, int);
void stringToVector(char *, char*,  double *, long long);

double cityBlockDistance(double *, double *, long long);
double euclidDistance(double *, double *, long long);

void subtract_vectors_abs(double *, double *, double *, long long);

my_bool compute_distance_init(UDF_INIT *initid, UDF_ARGS *args, char *message) {
	int ret = 0;

	if (args->arg_count != ARGUMENTS_COUNT) {
		strcpy(message, "Count of argument must be 5!\n");
		return 1;
	}

	if (args->arg_type[VECTOR1] != STRING_RESULT) {
		strcpy(message, "First argument must be a string!\n");
		ret = 1;
	}

	if (args->arg_type[VECTOR2] != STRING_RESULT) {
		strcpy(message, "Second argument must be a string!\n");
		ret = 1;
	}

	if (args->arg_type[VECTOR_SIZE] != INT_RESULT) {
		strcpy(message, "Third argument must be an integer, which represent lenght of both arrays!\n");
		ret = 1;
	}

	if (args->arg_type[DELIMITER] != STRING_RESULT) {
		strcpy(message, "Fifth argument must be a string, which represent delimiters for parsing vectro1 and vector2!\n");
		ret = 1;
	}
	
	if (args->arg_type[METHOD] != INT_RESULT) {
		strcpy(message, "Fourth argument must be 1 (for city block distance) or 2 (for Euclid distance)!\n");
		ret = 1;
	}

	int i;
	for (i = 0; i < ARGUMENTS_COUNT; i++) {
		constant_arg_compute_distance[i] = (args->args[i]) ? 1 : 0;
	}
	return ret;
}

void compute_distance_deinit(UDF_INIT *initid ) {
}

double compute_distance(UDF_INIT *initid, UDF_ARGS *args, char *is_null, char *error) {
	int vector1_size = args->lengths[VECTOR1];
	int vector2_size = args->lengths[VECTOR2];
	char vector1[vector1_size];
	char vector2[vector2_size];
	clear_char_array2(args->args[VECTOR1], vector1_size);
	strcpy(vector1, args->args[VECTOR1]);
//	vector1[vector1_size] = '\0'; // ? jaktoze to funguje, prirazuji na pozici pole ktere neni inicializovane

	clear_char_array2(args->args[VECTOR2], vector2_size);
	strcpy(vector2, args->args[VECTOR2]);
//	vector2[vector2_size] = '\0';

	long long size    = *((long long*) args->args[VECTOR_SIZE]);
	long long method  = *((long long*) args->args[METHOD]);
	char * delimiter = args->args[DELIMITER];

//	initid->ptr = (char *) malloc((vector2_size + 2) * sizeof(char));
//	sprintf(initid->ptr, ">%s<", vector2);

	double vec1[size];
	double vec2[size];

	stringToVector(vector1, delimiter, vec1, size);
	stringToVector(vector2, delimiter, vec2, size);

	double distance = 0.0;
	if (method == CBD) {
		distance = cityBlockDistance(vec1, vec2, size);
	} else if (method == ED) {
		distance = euclidDistance(vec1, vec2, size);
	} else {
		distance = -1;
	}
	
//	char test[256];
//	vectorToString(vec1, vec2, size, test);
//
//	int i = 0;
//	char c;
//	while((c = test[i]) != '\0') {
//		i++;
//	}
//	initid->ptr = (char *) malloc(i * sizeof(char));
//	strcpy(initid->ptr, test);
//	clear_char_array(test);

	// jedna-li se o vektor tahany z databaze, pak je treba
	// retezec po zpracovani vynulovat, jinak to dela problemy
	// pri zpracovani nasledujicicho radku.
	// Jednali se o konstatni vektor (zadany jako retezec
	// metode v dotazu) pak se naopak nesmi vymazat! Protoze
	// metoda pouziva stejny retezec pro vsechny radky tabulky

//	if (!constant_arg_compute_distance[VECTOR1]) {
//		clear_char_array(args->args[VECTOR1]);
//	}
//	if (!constant_arg_compute_distance[VECTOR2]) {
//		clear_char_array(args->args[VECTOR2]); 
//	}

	
//	return 1;
	return distance;
//	return initid->ptr;
}

my_bool compare_vectors_init(UDF_INIT *initid, UDF_ARGS *args, char *message) {
	int ret = 0;

	if (args->arg_count != ARGUMENTS_COUNT) {
		strcpy(message, "Count of argument must be 5!\n");
		return 1;
	}

	if (args->arg_type[VECTOR1] != STRING_RESULT) {
		strcpy(message, "First argument must be a string!\n");
		ret = 1;
	}

	if (args->arg_type[VECTOR2] != STRING_RESULT) {
		strcpy(message, "Second argument must be a string!\n");
		ret = 1;
	}

	if (args->arg_type[VECTOR_SIZE] != INT_RESULT) {
		strcpy(message, "Third argument must be an integer, which represent lenght of both arrays!\n");
		ret = 1;
	}

	if (args->arg_type[DELIMITER] != STRING_RESULT) {
		strcpy(message, "Fifth argument must be a string, which represent delimiters for parsing vectro1 and vector2!\n");
		ret = 1;
	}
	
	if (args->arg_type[TOLERATION] != INT_RESULT) {
		strcpy(message, "Fourth argument must be integer, that means: (value - toleration < value < value + toleration)!\n");
		ret = 1;
	}

	int i;
	for (i = 0; i < ARGUMENTS_COUNT; i++) {
		constant_arg_compare_vectors[i] = (args->args[i]) ? 1 : 0;
	}
	return ret;

}

void compare_vectors_deinit(UDF_INIT *initid) {
}

long long compare_vectors(UDF_INIT *initid, UDF_ARGS *args, char * is_null, char * error) {
	int vector1_size = args->lengths[VECTOR1]; // count of chars in a vector include ',';
	int vector2_size = args->lengths[VECTOR2];
	char vector1[vector1_size];
	char vector2[vector2_size];
	clear_char_array2(args->args[VECTOR1], vector1_size);
	strcpy(vector1, args->args[VECTOR1]);
//	vector1[vector1_size] = '\0';
	clear_char_array2(args->args[VECTOR2], vector2_size);
	strcpy(vector2, args->args[VECTOR2]);
//	vector2[vector2_size] = '\0';


	long long size    = *((long long*) args->args[VECTOR_SIZE]);
	char * delimiter = args->args[DELIMITER];
	long long toleration  = *((long long*) args->args[TOLERATION]);

	double vec1[size];
	double vec2[size];

	stringToVector(vector1, delimiter, vec1, size);
	stringToVector(vector2, delimiter, vec2, size);

	double subtract_vec1_vec2[size];
	subtract_vectors_abs(vec1, vec2, subtract_vec1_vec2, size);


	long long ret = 1;
	int i;
	for (i = 0; i < size; i++) {
		if (subtract_vec1_vec2[i] >= toleration) {
			ret = 0;	
			break;
		}
	}

//	char test[256];
//	vectorToString(subtract_vec1_vec2, size, test);
//
//	int i = 0;
//	char c;
//	while((c = test[i]) != '\0') {
//		i++;
//	}
//	initid->ptr = (char *) malloc(i * sizeof(char));
//	strcpy(initid->ptr, test);
//	clear_char_array(test);
//
	// vymazani dynamicky predavanych vektoru
//	if (!constant_arg_compare_vectors[VECTOR1]) {
//		clear_char_array(args->args[VECTOR1]);
//	}
//	if (!constant_arg_compare_vectors[VECTOR2]) {
//		clear_char_array(args->args[VECTOR2]); 
//	}

//	return initid->ptr;
	return ret;
}

void vectorToString(double *v1, double *v2, long long size, char *result) {
	int i;
	char buff[50];	
	for (i = 0; i < size; i++) {
		sprintf(buff, "(%f, %f), ", v1[i], v2[i]);
		strcat(result, buff);
		clear_char_array(buff);
	}
}
void clear_char_array(char * buff) {
	int i = 0;
	char c;
	while ((c = buff[i]) != '\0') {
		buff[i] = '\0';
		i++;
	}
}

void clear_char_array2(char *buff, int idx_from) {
	int i = idx_from;
	char c;
	while((c = buff[i]) != '\0') {
		buff[i] = '\0';
		i++;
	}
}

void stringToVector(char * in_vector, char * delimiters, double * out_vector, long long size) {
	
//	char * buff = strdup(in_vector);
	char *p = strtok(in_vector, delimiters);
	int i = 0;
	while (p || i < size) {
		out_vector[i] = atof(p);
		p = strtok(NULL, delimiters);
		i++;
	}
//	clear_char_array(buff);
}


double cityBlockDistance(double *v1, double *v2, long long size) {
	double cbd = 0.0;	
	int i;
	for (i = 0; i < size; i++) {
		double diff = v1[i] - v2[i];
		if (diff < 0) {
			diff *= (-1);
		}
		cbd += diff;
//		cbd += abs(v1[i] - v2[i]); // abs vraci integer.		
	}
	return cbd;
}

double euclidDistance(double *v1, double *v2, long long size) {
	double ed = 0.0;
	int i;
	for (i = 0; i < size; i++) {
		double diff = v1[i] - v2[i];
		ed += (diff * diff);
	}
	return sqrt(ed);
}

void subtract_vectors_abs(double *v1, double *v2, double *result, long long size) {
	int i;
	for (i = 0; i < size; i++) {
		double diff = v1[i] - v2[i];
		if (diff < 0) {
			diff *= (-1);
		}
		result[i] = diff;
	}
}
