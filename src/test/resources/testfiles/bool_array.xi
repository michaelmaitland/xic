use io

main(args : int[][]){
	x : bool[] = {true};
	y : bool[] = {false};
	z : bool[] = x + y;
	
	if(z[0]){
		println("Success!");
	}
	
	if(z[1]){
		println("Failure :(");
	}
}