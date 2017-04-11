#include <stdio.h>
#include <mpi.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <math.h>
#include <assert.h>
int num_rows;
int columns;

int decomp(int i, int size, int rows)
{
    size = size - 1;
    int r = (int) ceil( (double)rows / (double)size);
    int proc = i / r;
    return proc + 1;
}




int main(int argc, char** argv){
	MPI_Status status;
	const int MAX_NUMBERS = 100;
	int data[MAX_NUMBERS];
	int data1[MAX_NUMBERS];
	int counter = 0;
	int counter1 = 0;
	MPI_Init(&argc,&argv);
	int num_rows;
	  // Find out rank, size
    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);
    // We are assuming at least 3 pidsses for this task
    if (world_size < 3) {
      fprintf(stderr, "World size must be greater than 2 for %s\n", argv[0]);
      MPI_Abort(MPI_COMM_WORLD, 1);
    }

    if (world_rank == 0) {
     char const* const matrix = "matrix.txt";
     char const* const vector = "vector.txt";  
     FILE* mfile = fopen(matrix, "r"); /* should check the result */
     FILE* vfile = fopen(vector, "r");
     char  line[255];
     char *token;
     columns = 0;
     /*MATRIX PROCESSING*/
     while (fgets(line, sizeof(line), mfile) != NULL){   
	        char val1[255];
	        strcpy(val1,line);
  			token = strtok(val1, " ");
      	    while( token != NULL) 
		    {
		      if(strcmp(token, " ") != 0){
		      	if(counter == 0){
		      		num_rows = atoi(token);
		      	}
		      	data[counter] = atoi(token);
		      	counter++;
		      }
		      token = strtok(NULL, " ");
		    }
	 }
	 columns = (counter - 1) / num_rows; // 36 elements / 6 = 6 columns
	 int a[num_rows][columns];
	 int con = 1;
	 for(int z = 0; z < num_rows; z++){
	 	for(int x = 0; x < columns; x++){
	 		a[z][x] = data[con];
	 		con++;
	 	}
	 }
	 


	 /*VECTOR*/
     while (fgets(line, sizeof(line), vfile) != NULL){   
	        char val2[255];
	        strcpy(val2,line);
  			token = strtok(val2, " ");
      	    while( token != NULL) 
		    {
		      if(strcmp(token, " ") != 0){
		      	data1[counter1] = atoi(token);
		      	counter1++;
		      }
		      token = strtok(NULL, " ");
		    }
	 }
	 for(int j = 1; j < world_size; j++){
		MPI_Send(data1,counter1,MPI_INT,j,69,MPI_COMM_WORLD); 
		printf("sent vector data to %d\n", j);	
	 }
/*
	 int datalen = data[0] * columns;
	 int data2[datalen];
	 for(int i = 0 ; i < datalen; i++){
	 	data2[i] = data[i+1];
	 	printf("data2[%d]: %d\n", i, data2[i]);
	 }
*/

	 for(int y = 0; y < num_rows; y++){
	 	int proc = decomp(y,world_size,num_rows);
	 	MPI_Send(a[y],columns,MPI_INT,proc,(89*(y+1)),MPI_COMM_WORLD);
	 	printf("sent matrix data to %d\n",proc);
	 }

	 // now time to receive



	 int c[num_rows];
	 for(int i = 0; i < num_rows; i++){
	 	int src = decomp(i,world_size,num_rows);
	 	MPI_Recv(&c[i], 1, MPI_INTEGER, src, i, MPI_COMM_WORLD, &status);
        printf("P%d : c[%d]t= %d\n", world_rank, i, c[i]);
	 }
     fclose(mfile);
     fclose(vfile);
    } else { // FOR EVERY OTHER process
    	MPI_Request request;
		MPI_Probe(0, 69, MPI_COMM_WORLD, &status);
		MPI_Get_count(&status, MPI_INT, &counter1);
		int* number_buf = (int*)malloc(sizeof(int) * counter1);
		MPI_Recv(number_buf, counter1 , MPI_INT, 0, 69, MPI_COMM_WORLD, &status); /*Getting the vector*/
		for(int i = 0; i < counter1; i++){
			//printf("i,size,num_rows: %d, %d, %d\n", i, world_size, counter1);
			int pid = decomp(i,world_size,counter1);
			//printf("wr: %d, pid: %d\n", world_rank, pid);
			MPI_Probe(0, (89*(i+1)), MPI_COMM_WORLD, &status);
			MPI_Get_count(&status, MPI_INT, &columns);
			int* nbuf = (int*)malloc(sizeof(int) * (columns+1));
			if(pid == world_rank){
				MPI_Recv(nbuf,(columns+1),MPI_INT,0,(89*(i+1)), MPI_COMM_WORLD, &status);
				int sum = 0;
				for(int j = 0; j < columns; j++){
					printf("nbuf[%d]: %d\n",j, nbuf[j]);
					sum = sum + (number_buf[j]*nbuf[j]);
				}
				MPI_Send(&sum,1,MPI_INT,0,i,MPI_COMM_WORLD);
			}			
		}
		free(number_buf);

    }
    MPI_Barrier(MPI_COMM_WORLD);
    MPI_Finalize();
    return 0;

}
