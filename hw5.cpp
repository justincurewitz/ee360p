#include <stdio.h>
#include <mpi.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <math.h>
#include <assert.h>
#include <vector>
#include <iostream>
#include <fstream>
#include <sstream>
using namespace std;

int decomp(int i, int size, int rows)
{
    size = size - 1;
    int r = (int) ceil( (double)rows / (double)size);
    int proc = i / r;
    return proc + 1;
}

void mult(int Count,int *Sum,int Vec[],int Data[],int Column)
{
  int i=0,j=0, k=0;
  while(i<Count)
  {
    Sum[i]=0;
    for(j=0;j<Column;j++)
    {
       Sum[i] = Sum[i] + Data[j] * Vec[j];
       k++;
    }
    i++;
  }
}/*
void mult(int* matrix, int* vector, int count, int col_size, int* sum) {
	int num_rows = count/col_size;
	for (int i = 0; i < num_rows; i++){
		sum[i] = 0;
		for (int j = 0; j < col_size; j++) {
			sum[i] = sum[i] + (matrix[(i*col_size)+j]*vector[j]);
		}
	}
}
*/
int main(int argc, char** argv){
	int rows, cols, w_size;
	vector<int> dataMatrix;
	vector<int> vec;
	MPI_Status status;
	int counter = 0;	
	MPI_Init(&argc,&argv);
	// Find out rank, size
	int w_rank;
	MPI_Comm_rank(MPI_COMM_WORLD, &w_rank);
	MPI_Comm_size(MPI_COMM_WORLD, &w_size);
	int *sendCount = (int*)calloc(w_size, sizeof(int));
	int *disp = (int*)calloc(w_size, sizeof(int));
	int * vect;
	int* sendbuf;
	if (w_rank == 0) {
	 	/*MATRIX PROCESSING*/
		char const* const matrix = "matrix.txt";
	 	char const* const vector = "vector.txt"; 
		char line[255];
		char *token;
	 	FILE* mfile = fopen(matrix, "r"); /* should check the result */
	 	FILE* vfile = fopen(vector, "r");
	 /*MATRIX PROCESSING*/
	 	while (fgets(line, sizeof(line), mfile) != NULL){   
			char val1[255];
			strcpy(val1,line);
			token = strtok(val1, " ");
			while( token != NULL) 
			{
				if(strcmp(token, " ") != 0){
					if(counter == 0){
						rows = atoi(token);
						counter++;
					}else {
						dataMatrix.push_back(atoi(token));
						counter++;
					}
				}
				token = strtok(NULL, " ");
			}
	 }
		cols = dataMatrix.size()/rows;
 		/*VECTOR*/
	 	while (fgets(line, sizeof(line), vfile) != NULL){   
			char val2[255];
			strcpy(val2,line);
			token = strtok(val2, " ");
			while( token != NULL) 
			{
				if(strcmp(token, " ") != 0){
					vec.push_back(atoi(token));
				}
				token = strtok(NULL, " ");
			}
 		}
		fclose(mfile);
		fclose(vfile);

		//fin io
		#ifdef debug
		cout << "cols: " << cols << " vec size " << vec.size() << endl;
		#endif
		//determine sendcount and displacement
		if (rows/w_size >= 1) {
			for (int i = 0; i < w_size; i++) {
				sendCount[i] = rows/w_size * cols;
			}
		}
		for (int i = 0; i < rows%w_size; i++) {
			sendCount[i] += cols;
		}
		#ifdef debug
		cout << "w_size: " << w_size << endl;
		for (int i = 0; i < w_size; i++) {
			cout << sendCount[i];
		}
		cout << endl;
		#endif
		disp[0] = 0;
		for (int i = 1; i < w_size; i++) {
			disp[i] = sendCount[i-1] + disp[i - 1];
		}
		#ifdef debug
		cout << "w_size: " << w_size << endl;
		for (int i = 0; i < w_size; i++) {
			cout << disp[i] << " ";
		}
		#endif
		//allocate and put data in sendbuf
		sendbuf = (int*)malloc(dataMatrix.size()*sizeof(int));
		for (int i = 0; i < dataMatrix.size(); i++) {
			sendbuf[i] = dataMatrix[i];
		}
		cout << endl;
		vect = (int*)malloc(vec.size()*sizeof(int));
		for (int i = 0; i < vec.size(); i++) {
			vect[i] = vec[i];
		}
	}
	//give everyone row, col, vec, sendCount, disp
	for (int i = 0; i < w_size; i++) {
		//cout << vect[i] << endl;
		cout  << "rank " << w_rank<< " ";
		cout << sendCount[i] << " ";
		cout << disp[i] << " ";
	}
	cout << endl;
  MPI_Bcast(&rows,1,MPI_INT,0,MPI_COMM_WORLD);
  MPI_Bcast(&cols,1,MPI_INT,0,MPI_COMM_WORLD);
  if (w_rank != 0) {
		vect = (int*)malloc(sizeof(int) *cols);
	}
	assert(vect != NULL);
	MPI_Bcast(vect,cols,MPI_INT,0,MPI_COMM_WORLD);
  MPI_Bcast(sendCount,w_size,MPI_INT,0,MPI_COMM_WORLD);
  MPI_Bcast(disp,w_size,MPI_INT,0,MPI_COMM_WORLD);
	//make receive buffer
	int* recvbuf = (int*)malloc(sendCount[w_rank]*sizeof(int));
	//send dataMatrix to all processes
	MPI_Scatterv(sendbuf, sendCount, disp, MPI_INT, recvbuf, sendCount[w_rank], MPI_INT, 0, MPI_COMM_WORLD);
	int* sum = (int*)malloc(sizeof(int)*sendCount[w_rank]/cols);
	for (int i = 0; i < w_size; i++) {
		cout << sum[i] << " ";
	}
	mult(sendCount[w_rank]/cols, sum, vect, recvbuf, cols);
	MPI_Finalize();
	return 0;
}
