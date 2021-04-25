	.file	"fact.c"
	.intel_syntax noprefix
	.text
	.globl	_Ifactorial
	.type	_Ifactorial, @function
_Ifactorial:
	push	rbp
	mov	rbp, rsp
	sub	rsp, 16
	mov	QWORD PTR [rbp-8], rdi
	cmp	QWORD PTR [rbp-8], 1
	jg	.L2
	mov	eax, 1
	jmp	.L3
.L2:	
	mov	rax, QWORD PTR [rbp-8]
	sub	rax, 1
	mov	rdi, rax
	call	_Ifactorial
	imul	rax, QWORD PTR [rbp-8]
.L3:
	leave
	ret
	.size	_Ifactorial, .-_Ifactorial
	.globl	prompt
	.data
	.align 32
	.type	prompt, @object
	.size	prompt, 64
prompt:
	.quad	7
	.quad	78
	.quad	117
	.quad	109
	.quad	98
	.quad	101
	.quad	114
	.quad	63
	.globl	is
	.align 32
	.type	is, @object
	.size	is, 48
is:
	.quad	5
	.quad	33
	.quad	32
	.quad	105
	.quad	115
	.quad	32
	.text
	.globl	_Imain_paai
	.type	_Imain_paai, @function
_Imain_paai:
	push	rbp
	mov	rbp, rsp
	sub	rsp, 64
	mov	QWORD PTR [rbp-56], rdi
	jmp	.L5
.L6:
	mov	eax, OFFSET FLAT:prompt+8
	mov	rdi, rax
	call	_Iprint_pai
	call	_Ireadln_ai
	mov	QWORD PTR [rbp-16], rax
	mov	rax, QWORD PTR [rbp-16]
	mov	rdi, rax
	call	_IparseInt_t2ibai
	mov	QWORD PTR [rbp-24], rax
	mov	rax, QWORD PTR [rbp-8]
#APP
# 30 "fact.c" 1
	movq %rdx, rax
# 0 "" 2
#NO_APP
	mov	QWORD PTR [rbp-8], rax
	cmp	QWORD PTR [rbp-8], 0
	je	.L5
	mov	rax, QWORD PTR [rbp-24]
	mov	rdi, rax
	call	_Ifactorial
	mov	QWORD PTR [rbp-32], rax
	mov	rax, QWORD PTR [rbp-24]
	mov	rdi, rax
	call	_IunparseInt_aii
	mov	QWORD PTR [rbp-40], rax
	mov	eax, OFFSET FLAT:is+8
	mov	rdi, rax
	call	_Iprint_pai
	mov	rax, QWORD PTR [rbp-32]
	mov	rdi, rax
	call	_IunparseInt_aii
	mov	QWORD PTR [rbp-40], rax
	mov	rax, QWORD PTR [rbp-40]
	mov	rdi, rax
	call	_Iprintln_pai
.L5:
	call	_Ieof_b
	test	rax, rax
	je	.L6
	nop
	leave
	ret
	.size	_Imain_paai, .-_Imain_paai
	.ident	"GCC: (Ubuntu 7.4.0-1ubuntu1~18.04.1) 7.4.0"
	.section	.note.GNU-stack,"",@progbits
