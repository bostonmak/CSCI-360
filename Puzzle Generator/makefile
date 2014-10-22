CC = g++
CFLAGS = -O3
EXEC = PuzzleGenerator
	
all:
	$(CC) $(CFLAGS) -o $(EXEC) main.cpp
	
clean:
	rm -f $(EXEC)

test:
	make clean		
	make all
	./$(EXEC) 5 5 2 4
	