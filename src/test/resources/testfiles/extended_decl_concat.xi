use io

main(args : int[][]){
	x: int[][] = {"Hello" , "how"}
	y: int[][] = {"are", "you", "?!"}
	z: int[][] = x + y;
	
	i:int = 0;
	
	while(i < length(z)){
		print(z[i] + " ");
		i = i + 1;
	}
}