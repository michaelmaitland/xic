use io

main(args : int[][]){
	layer1 : int[] = {1, 0, 0, 0, 1}
	layer2 : int[] = {1, 1, 0, 0, 0}
	layer3 : int[] = {1, 0, 1, 1, 0}
	layer4 : int[] = {0, 0, 1, 0, 0}
	layer5 : int[] = {1, 0, 0, 1, 0}
	
	ocean : int[][] = {layer1, layer2, layer3, layer4, layer5};
	
	println("Number of islands: " + unparseInt(countIslands(ocean))); // 5
}

countIslands(ocean : int[][]) : int{
	i : int = 0;
	j : int = 0;
	numIslands : int = 0;
	
	while (i < length(ocean)){
		while(j < length(ocean[0])){
			if(ocean[i][j] == 1){
				numIslands = numIslands + 1;
				removeIsland(ocean, i, j);
			}
			j = j + 1;
		}
		i = i + 1;
		j = 0;
	}
	
	return numIslands;
}

removeIsland(ocean : int[][], x : int, y : int){
	if(x < 0 | x >= length(ocean)){ return;}
	if(y < 0 | y >= length(ocean[0])){ return;}
	if(ocean[x][y] == 0) { return;}
	
	ocean[x][y] = 0;
	
	removeIsland(ocean, x - 1, y);
	removeIsland(ocean, x + 1, y);
	removeIsland(ocean, x, y - 1);
	removeIsland(ocean, x, y + 1);
}