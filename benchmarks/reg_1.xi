main(args: int[][]) {
  i:int = 0
  while(i < 20000){
  	b:bool = isprime(i)
  	i = i+1
  }
}
gcd(a:int, b:int):int {
    while (a != 0) {
        if (a<b) b = b - a else a = a - b
    }
    return b
}
isprime(n:int):bool {
    i:int = 2
    while (i*i <= n) {
        if (gcd(i, n) != 1) { return false }
        i = i+1
    } return true
}