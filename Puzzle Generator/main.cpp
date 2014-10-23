/*
NAME: Boston Mak
STUDENT ID: 7305093692
*/

#include <cstdlib>
#include <cstdio>
#include <stdio.h>
#include <iostream>
#include <sys/time.h>
#include <stdio.h>
#include <time.h>
#include <vector>
#include "Timer.h"

using namespace std;


struct Location
{
	int x;
	int y;

	Location()
	{
		x = 0;
		y = 0;
	}
	Location(int xLoc, int yLoc)
	{
		x = xLoc;
		y = yLoc;
	}

};

struct Cell
{
	Cell* parent;
	Location* location;
	vector<Cell*> connections;
	int val;
	int touchDepth;
	bool touched;
	bool isReachable;
	bool isReaching;

	Cell()
	{
		val = 0;
		touched = false;
		isReachable = false;
		isReaching = false;
	}

	Cell(int value, int xLoc, int yLoc)
	{
		location = new Location(xLoc, yLoc);
		val = value;
		touched = false;
		isReachable = false;
		isReaching = false;
	}

	void ToString()
	{
		printf("(%d, %d)", location->x, location->y);
	}
};

struct Grid
{
	Cell* grid[10][10];
	vector<Cell*> whiteHoles;
	vector<Cell*> blackHoles;
	vector<Cell*> forcedForwardMoves;
	vector<Cell*> forcedBackwardMoves;
	vector<Cell*> solution;
	int solutionLength;
	int numRows;
	int numCols;
	bool hasSolution;
	bool isUnique;


	Grid(int rows, int cols, int min, int max)
	{
		hasSolution = false;
		isUnique = false;
		for (int x = 0; x < rows; x++)
		{
			for (int y = 0; y < cols; y++)			
			{
				grid[x][y] = new Cell(rand() % (max - min + 1) + min, x, y);
			}
		}
		// set goal.
		grid[rows - 1][cols - 1]->val = 0;
		solutionLength = 0;
		numRows = rows;
		numCols = cols;
	}

	void SetConnections()
	{
		for (int x = 0; x < numRows; x++)
		{
			for (int y = 0; y < numCols; y++)
			{
				if (grid[x][y]->val > 0)
				{
					if (x + grid[x][y]->val < numRows)
					{
						grid[x][y]->connections.push_back(grid[x + grid[x][y]->val][y]);
						printf("(%d, %d) of val %d connects to (%d, %d) \n", x, y, grid[x][y]->val, x + grid[x][y]->val, y);
					}
					if (x - grid[x][y]->val >= 0)
					{
						grid[x][y]->connections.push_back(grid[x - grid[x][y]->val][y]);
						printf("(%d, %d) of val %d connects to (%d, %d) \n", x, y, grid[x][y]->val, x - grid[x][y]->val, y);
					}
					if (y + grid[x][y]->val < numCols)
					{
						grid[x][y]->connections.push_back(grid[x][y + grid[x][y]->val]);
						printf("(%d, %d) of val %d connects to (%d, %d) \n", x, y, grid[x][y]->val, x, y + grid[x][y]->val);
					}
					if (y - grid[x][y]->val >= 0)
					{
						grid[x][y]->connections.push_back(grid[x][y - grid[x][y]->val]);
						printf("(%d, %d) of val %d connects to (%d, %d) \n", x, y, grid[x][y]->val, x, y - grid[x][y]->val);
					}
				}
			}
		}
	}

	void ToString()
	{
		printf("Generated puzzle: \n");
		for (int y = 0; y < numCols; y++)
		{
			for (int x = 0; x < numRows; x++)
			{
				printf("%d ", (grid[x][y])->val);
			}
			printf("\n");
		}
	}
};

void FindSolution(Grid* g)
{
	int depth = 0;
	Cell* prev = NULL;
	Cell* current = g->grid[0][0];
	Cell* next = current->connections[0];
	current->parent = NULL;
	current->touched = true;
	current->touchDepth = depth;
	current->isReachable = true;
	// for int x/y, if cell !touched, pathfind to check for white holes.
	for (int y = 0; y < g->numCols; y++)
	{
		for (int x = 0; x < g->numRows; x++)
		{
			if (!g->grid[x][y]->touched)
			{
				while (!g->grid[x][y]->connections.empty())
				{
					// If dead end found, backtrack.
					if (next == NULL)
					{
						Cell* old = current;
						// cout << "Backing" << endl;
						--depth;
						current = prev;
						prev = prev->parent;
						current->connections.erase(current->connections.begin());
						if (!current->connections.empty())
						{
							next = current->connections[0];
						}
						else
						{
							next = NULL;
						}
						if (old->val != 0 && !old->isReaching)
						{
							old->isReaching = false;
						}
						printf("Moved back from (%d, %d) to (%d, %d) \n", old->location->x, old->location->y, current->location->x, current->location->y);
					}
					// If shorter path already found, remove the connection.
					else if (next->touched && next->touchDepth <= depth)
					{
						// cout << "Redirecting" << endl;
						current->connections.erase(current->connections.begin());
						if (!current->connections.empty())
						{
							next = current->connections[0];
						}
						else
						{
							next = NULL;
						}
					}
					else
					{
						// cout << "Continuing" << endl;
						++depth;
						prev = current;
						current = next;
						if (!current->connections.empty())
						{
							next = current->connections[0];
						}
						else
						{
							if (!current->touched)
							{
								//Dead end.
								g->blackHoles.push_back(current);
							}
							next = NULL;
						}
						current->touched = true;
						current->touchDepth = depth;
						if (x == 0 && y == 0)
						{
							current->isReachable = true;
						}
						else if (!current->isReachable)
						{
							current->isReachable = false;
						}
						current->parent = prev;
						printf("Moved from (%d, %d) of value %d to (%d, %d) \n", prev->location->x, prev->location->y, prev->val, current->location->x, current->location->y);
						if (current->val == 0)
						{
							printf("Solution of depth %d found \n", depth);
							Cell* solutionCell = current;
							while (solutionCell != NULL)
							{
								solutionCell->isReaching = true;
								if (x == 0 && y == 0 && (g->solutionLength > depth || depth == 0))
								{
									g->solution.insert(g->solution.begin(), solutionCell);
								}
								solutionCell = solutionCell->parent;
							}
						}
					}
				}

				cout << "dfs done" << endl;
			}
		}
	}
	
}

void GeneratePuzzle(int nRows, int nColumns, int minVal, int maxVal)
{
	srand(time(NULL));
	Grid* grid = new Grid(nRows, nColumns, minVal, maxVal);
	grid->SetConnections();
	while (!grid->hasSolution)
	{
		break;
	}
	grid->ToString();
	FindSolution(grid);
}



int main(int argc, char **argv)
{
	// Process the arguments
	if (argc != 5)
	{
		printf("Please specify the number of rows and columns and the minimum and maximum values for each cell (requires 4 parameters)\n");
		exit(0);
	}
	int nRows = atoi(argv[1]);
	int nColumns = atoi(argv[2]);
	int minVal = atoi(argv[3]);
	int maxVal = atoi(argv[4]);

	// Start the timer
	Timer t;
	t.StartTimer();
  	
	// Generate the puzzle
	printf("Generating a %dx%d puzzle with values in range [%d-%d]\n", nRows, nColumns, minVal, maxVal);
	
	GeneratePuzzle(nRows, nColumns, minVal, maxVal);
		
	// Print the elapsed time
    printf("Total time: %.6lf seconds\n", t.GetElapsedTime());

	return 0;
}
