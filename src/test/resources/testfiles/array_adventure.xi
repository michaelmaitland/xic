use io

main(args : int[][]){
	x : int[4][4][4];
	
	i : int = 0;
	j : int = 0;
	k : int = 0;
	counter : int = 0;
	
	while(i < length(x)){
		while(j < length(x[0])){
			while(k < length(x[0][0])){
				x[i][j][k] = 1;
				counter = counter + x[i][j][k];
				k = k + 1;
			}
			j = j + 1;
			k = 0;
		}
		i = i + 1;
		j = 0;
	}
	
	print(unparseInt(counter));
}