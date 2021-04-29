use io
use conv

main(args : int[][]){

	print(unparseInt(0 *>> 9000000000000000000)); // 0  
	print(unparseInt(-1 *>> 2)); // 1  
	print(unparseInt(1 *>> 9000000000000000000)); // 0
	print(unparseInt(9000000000000000000 *>> 9000000000000000000)); // 4391018798566292957 
}