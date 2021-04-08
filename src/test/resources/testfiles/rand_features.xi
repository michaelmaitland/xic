use io

main(args : int[][]){
	println("Hello" + unparseInt(6));
	
	println(unparseInt(getSmallerPosNumber(4, 5)))
	println(unparseInt(getSmallerPosNumber(8, 10)))
	println(unparseInt(getSmallerPosNumber(-2, 1)))
	
	println(unparseInt(recProduct(3, 3)))
	println(unparseInt(recProduct(-3, 5)))
	
	
}

getSmallerPosNumber(x : int, y : int) : int{
	tx : int = x;
	ty : int = y;

	while(tx != 0 & ty != 0){
		tx = tx - 1;
		ty = ty - 1;
	}
	
	if(tx == 0){
		return x;
	}	
	else if(ty == 0){
		return y;
	}
	else {
		return -1;	
	}
}

recProduct(x : int, y : int) : int{
	if(y == 0) {
		return 0;
	}
	else{
		return x + recProduct(x, y-1);
	}
}