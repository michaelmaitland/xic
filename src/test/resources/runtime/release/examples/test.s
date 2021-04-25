	.intel_syntax noprefix
	.text
	.globl	_Imain_paai
	.type	_Imain_paai, @function
_Imain_paai:
    push rbp
	mov	rbp, rsp
    
    mov rdi, 1
    mov rax, 2
    add rdi, rax
    call _IunparseInt_aii
    mov rdi, rax
    call _Iprint_pai

    mov rsp, rbp
    pop rbp
    ret

