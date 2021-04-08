use io

main(args : int[][]){
	x : int[4][4][4];
	y : int[0][0][0];
	z : int[1][2][3]
	
	println(unparseInt(getDimProd(x)));
	println(unparseInt(getDimProd(y)));
	println(unparseInt(getDimProd(z)));
	
}

getDimProd(x : int[][][]) : int{
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
	
	return counter;
}