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

void mult(int count,int *sum,int vec[],int data[],int column)
{
  int i=0,j=0,l=0,k=0;
  int kount = 0;
  while(i<count)
  {
    sum[i]=0;
    for(j=0;j<column;j++) {  
       int temp = sum[i];
       sum[i] = sum[i] + data[k] * vec[j];
       //printf("sum: %d = %d + %d * %d\n", sum[i], temp, data[k], vec[j]);
       k++;
    }
    i++;
  }
}


/*
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
	int *reccount = (int*)calloc(w_size, sizeof(int));
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
		sendCount[0] = 0;
		//determine sendcount and displacement
		if (rows/(w_size-1) >= 1) {
			for (int i = 1; i < w_size; i++) {
				sendCount[i] = rows/(w_size - 1) * cols;
			}
		}
		for (int i = 1; i < rows% ( w_size - 1) ; i++) {
			sendCount[i] += cols;
		}
		
		disp[1] = 0;
		for (int i = 2; i < w_size; i++) {
			disp[i] = sendCount[i-1] + disp[i - 1];
		}
		
		//allocate and put data in sendbuf
		sendbuf = (int*)malloc(dataMatrix.size()*sizeof(int));
		for (int i = 0; i < dataMatrix.size(); i++) {
			sendbuf[i] = dataMatrix[i];
		}
		vect = (int*)malloc(vec.size()*sizeof(int));
		for (int i = 0; i < vec.size(); i++) {
			vect[i] = vec[i];
		}
	  
	}
	//give everyone row, col, vec, sendCount, disp
    MPI_Bcast(&rows,1,MPI_INT,0,MPI_COMM_WORLD);
    MPI_Bcast(&cols,1,MPI_INT,0,MPI_COMM_WORLD);
    if (w_rank != 0) {
		vect = (int*)malloc(sizeof(int) *cols);
	}
	MPI_Bcast(vect,cols,MPI_INT,0,MPI_COMM_WORLD);
    MPI_Bcast(sendCount,w_size,MPI_INT,0,MPI_COMM_WORLD);
    MPI_Bcast(disp,w_size,MPI_INT,0,MPI_COMM_WORLD);
	//make receive buffer
	int* recvbuf = (int*)malloc(sendCount[w_rank]*sizeof(int));
	//send dataMatrix to all processes





	MPI_Scatterv(sendbuf, sendCount, disp, MPI_INT, recvbuf, sendCount[w_rank], MPI_INT, 0, MPI_COMM_WORLD);
	int count=sendCount[w_rank]/cols;
	int* sum = (int*)malloc(sizeof(int)*sendCount[w_rank]/cols);
	if(w_rank != 0){
		mult(count, sum, vect, recvbuf, cols);
	}
	int *result=(int *)calloc(sizeof(int),rows);
    disp[0]=0;
    reccount[0]=sendCount[0]/cols;  
    for(int i=1;i<w_size;i++)
    {
    	disp[i] = disp[i-1] + sendCount[i-1]/cols; 
    	reccount[i]=sendCount[i]/cols;
    }
    MPI_Gatherv(sum,count,MPI_INT,result,reccount,disp,MPI_INT,0,MPI_COMM_WORLD);
    if(w_rank==0){
    FILE *fp;
	fp=fopen("result.txt", "w");
	if(fp == NULL)
	    exit(-1);
    for(int i=0;i<rows;i++)
    {
       fprintf(fp,"%d\n",result[i]);
    }
	fclose(fp);
    }
	MPI_Finalize();
	return 0;
}
