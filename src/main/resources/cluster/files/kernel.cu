
#include "cuda_runtime.h"
#include "device_launch_parameters.h"

#include <iostream>
#include <stdio.h>

#define BLOCK_SIZE 8
#define N 10000

/*
* Блок 1    Время   0.082880    Нитей 256
* Блок 8    Время   0.087264
* Блок 16   Время   0.095488
* Блок 32   Время   0.138336
* Блок 64   Время   0.284768
* Блок 128  Время   0.991424
* 
* Блок 128  Время   0.166720    Нитей 16
* Блок 64   Время   0.103435
* Блок 16   Время   0.107431
* Блок 1    Время   0.078080
* 
* Блок 1    Время   0.083536    Нитей 64
* Блок 8    Время   0.069653                
* Блок 16   Время   0.079514    
* Блок 64   Время   0.139431    
* 
* В целом заметно, что небольшие блоковые структуры достаточно сильно снижают затраты по времени, однако это
* также зависит и от количества тредов в самом блоке, поэтому перебор логично совершать по двум критериям,
* если интересует оптимальное время
* 
*/

__global__ void subKernel(int *a, int *b, int cols)
{
    int idx = cols * (blockDim.y * blockIdx.y + threadIdx.y) + blockDim.x * blockIdx.x + threadIdx.x;
    a[idx] -= b[idx];
}

int main()
{
    int** a = new int* [N];
    int** b = new int* [N];

    srand(NULL);

    for (size_t i = 0; i < N; i++)
    {
        a[i] = new int[N];
        b[i] = new int[N];
    }

    for (size_t i = 0; i < N; i++)
    {
        for (size_t j = 0; j < N; j++)
        {
            a[i][j] = rand() % 100;
            b[i][j] = rand() % 100;
        }
    }

    int* dev_a;
    int* dev_b;

    cudaMalloc((void**)&dev_a, N * N * sizeof(int));
    cudaMalloc((void**)&dev_b, N * N * sizeof(int));

    dim3 threadsInBlock = dim3(8, 4);
    dim3 blocksInGrid = dim3(BLOCK_SIZE, BLOCK_SIZE);

    int* a_line = new int[N * N];
    int* b_line = new int[N * N];

    for (size_t i = 0; i < N; i++)
    {
        for (size_t j = 0; j < N; j++)
        {
            a_line[i * N + j] = a[i][j];
            b_line[i * N + j] = b[i][j];
        }
    }

    /*for (size_t i = 0; i < 10; i++)
    {
        for (size_t j = 0; j < 10; j++)
        {
            printf("%d\t", a[i][j]);
        }
        printf("\n");
    }*/
    
    cudaMemcpy(dev_a, a_line, N * N * sizeof(int), cudaMemcpyHostToDevice);
    cudaMemcpy(dev_b, b_line, N * N * sizeof(int), cudaMemcpyHostToDevice);

    cudaEvent_t start, stop;
    float time;
    cudaEventCreate(&start);
    cudaEventRecord(start, 0);

    subKernel <<< blocksInGrid, threadsInBlock >>> (dev_a, dev_b, N);

    cudaEventCreate(&stop);
    cudaEventRecord(stop, 0);
    cudaEventSynchronize(stop);
    cudaEventElapsedTime(&time, start, stop);

    cudaMemcpy(a_line, dev_a, N * N *sizeof(int), cudaMemcpyDeviceToHost);

    

    cudaDeviceReset();

    for (size_t i = 0; i < 10; i++)
    {
        for (size_t j = 0; j < 10; j++)
        {
            printf("%d\t", a_line[i * N + j]);
        }
        printf("\n");
    }
    printf("Time in millis: %f", time);
}