use io

main(args : int[][]){
	printEightIntSum(1, 2, 3, 4, 5, 6, 7, 8);
	
	a:int, b:int, c:int, d:int = returnEvenIdxInts(1, 2, 3, 4, 5, 6, 7, 8);
	
	println(unparseInt(a + b + c + d));
}

printEightIntSum(i1:int, i2:int, i3:int, i4:int, i5:int, i6:int, i7:int, i8:int){
	println(unparseInt(i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8));
}

returnEvenIdxInts(i1:int, i2:int, i3:int, i4:int, i5:int, i6:int, i7:int, i8:int) : int, int, int, int{
	return i2, i4, i6, i8;
}
