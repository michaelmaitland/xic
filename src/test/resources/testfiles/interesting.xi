use io

main(args : int[][]){
	arr : int[] = {0, 0, 0, 0, 0}
	
	setArrVal(arr, 0, 1);
	setArrVal(arr, 1, 4 / 2);
	setArrVal(arr, 2, 8 % 5);
	setArrVal(arr, 3, 2 * 6 /3);
	setArrVal(arr, 4, 5 * 5 / 5 * 5 / 5);
	
	a : int, b : int, c : int, d: int , e : int = emptyArr(arr);
	
	println(unparseInt(a + b + c + d + e));

}

setArrVal(arr : int[], idx : int, val : int){
	arr[idx] = val;
}

emptyArr(arr : int[]) : int, int, int, int, int{
	return arr[0], arr[1], arr[2], arr[3], arr[4];
}