	.file	"arr.c"
	.intel_syntax noprefix
	.text
	.globl	_Imain_paai
	.type	_Imain_paai, @function
_Imain_paai:
	push	rbp
	mov	rbp, rsp
	sub	rsp, 32
	mov	QWORD PTR [rbp-24], rdi
	mov	rax, QWORD PTR [rbp-24]
	mov	rax, QWORD PTR [rax-8]
	mov	QWORD PTR [rbp-16], rax
	mov	QWORD PTR [rbp-8], 0
	jmp	.L2
.L3:
	mov	rax, QWORD PTR [rbp-8]
	lea	rdx, [0+rax*8]
	mov	rax, QWORD PTR [rbp-24]
	add	rax, rdx
	mov	rax, QWORD PTR [rax]
	mov	rdi, rax
	call	_Iprintln_pai
	add	QWORD PTR [rbp-8], 1
.L2:
	mov	rax, QWORD PTR [rbp-8]
	cmp	rax, QWORD PTR [rbp-16]
	jl	.L3
	nop
	leave
	ret
	.size	_Imain_paai, .-_Imain_paai
	.ident	"GCC: (Ubuntu 7.4.0-1ubuntu1~18.04.1) 7.4.0"
	.section	.note.GNU-stack,"",@progbits
