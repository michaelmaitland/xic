.file "unitTest.xi"
.intel_syntax noprefix
.text
.globl _Imain_paai
.type _Imain_paai, @function
_Imain_paai:
push rbp
mov rbp, rsp
sub rsp, 64
mov r9, rdi
mov [rbp + -56], r9
mov r9, 16
mov [rbp + -48], r9
lea r9, [rsp + -8]
mov [rbp + -64], r9
mov r9, [rbp + -64]
push r9
mov r9, [rbp + -48]
mov rdi, r9
call _xi_alloc
mov r9, rax
mov [rbp + -40], r9
add rsp, 8
mov r9, [rbp + -40]
mov [r9], 1
mov r9, [rbp + -40]
lea r10, [r9 + 8]
mov [rbp + -32], r10
mov r9, [rbp + -32]
mov [r9], 97
mov r9, [rbp + -40]
lea r10, [r9 + 8]
mov [rbp + -8], r10
mov r9, [rbp + -8]
mov r10, r9
mov [rbp + -16], r10
lea r9, [rsp + -8]
mov [rbp + -24], r9
mov r9, [rbp + -24]
push r9
mov r9, [rbp + -16]
mov rdi, r9
call _Iprint_pai
add rsp, 8
mov rsp, rbp
pop rbp
ret
_I$allocLayer_piiiiii:
push rbp
mov rbp, rsp
sub rsp, 320
mov r9, rdi
mov [rbp + -168], r9
mov r9, rsi
mov [rbp + -208], r9
mov r9, rdx
mov [rbp + -176], r9
mov r9, rcx
mov [rbp + -184], r9
mov r9, r8
mov [rbp + -224], r9
mov r9, r9
mov [rbp + -240], r9
mov r9, 0
mov [rbp + -112], r9
mov r9, 0
mov [rbp + -96], r9
mov r9, [rbp + -168]
lea r10, [r9 + 1]
mov [rbp + -16], r10
mov r9, [rbp + -16]
mov r10, r9
mov [rbp + -136], r10
header:
mov r9, [rbp + -96]
mov r10, r9
mov [rbp + -120], r10
mov r9, [rbp + -136]
imul r10, r9
mov [rbp + -120], r10
mov r9, [rbp + -112]
mov r10, [rbp + -120]
lea r11, [r9 + r10]
mov [rbp + -144], r11
mov r9, [rbp + -144]
mov r10, r9
mov [rbp + -152], r10
mov r9, [rbp + -176]
cmp r10, r9
mov [rbp + -152], r10
setl al
mov r9, rax
mov [rbp + -56], r9
mov r9, 1
mov [rbp + -72], r9
mov r9, [rbp + -56]
mov r10, r9
mov [rbp + -88], r10
mov r9, [rbp + -72]
xor r10, r9
mov [rbp + -88], r10
cmp r9, 1
mov [rbp + -88], r9
je done
mov r9, 0
mov [rbp + -48], r9
mov r9, [rbp + -48]
cmp r10, r9
mov [rbp + -112], r10
sete al
mov r9, rax
mov [rbp + -192], r9
cmp r9, 1
mov [rbp + -192], r9
je after
mov r9, [rbp + -208]
lea r10, [r9 + 1]
mov [rbp + -312], r10
mov r9, [rbp + -168]
mov r10, r9
mov [rbp + -320], r10
mov r9, [rbp + -312]
imul r10, r9
mov [rbp + -320], r10
mov r9, [rbp + -96]
mov r10, r9
mov [rbp + -248], r10
mov r9, [rbp + -320]
imul r10, r9
mov [rbp + -248], r10
mov r9, 1
mov [rbp + -256], r9
mov r9, [rbp + -112]
mov r10, r9
mov [rbp + -280], r10
mov r9, [rbp + -256]
sub r10, r9
mov [rbp + -280], r10
mov r9, [rbp + -208]
lea r10, [r9 + 1]
mov [rbp + -8], r10
mov r9, [rbp + -280]
mov r10, r9
mov [rbp + -24], r10
mov r9, [rbp + -8]
imul r10, r9
mov [rbp + -24], r10
mov r9, [rbp + -24]
mov r10, [rbp + -224]
lea r11, [r9 + r10]
mov [rbp + -104], r11
mov r9, [rbp + -248]
mov r10, [rbp + -104]
lea r11, [r9 + r10]
mov [rbp + -128], r11
mov r9, [rbp + -128]
mov r10, r9
mov [rbp + -264], r10
mov r9, 8
mov [rbp + -160], r9
mov r9, [rbp + -160]
mov r10, r9
mov [rbp + -64], r10
mov r9, [rbp + -264]
imul r10, r9
mov [rbp + -64], r10
mov r9, [rbp + -240]
mov r10, [rbp + -64]
lea r11, [r9 + r10]
mov [rbp + -80], r11
mov r9, [rbp + -80]
mov r10, r9
mov [rbp + -288], r10
mov r9, [rbp + -288]
mov r10, [rbp + -208]
mov [r9], r10
mov r9, 8
mov [rbp + -200], r9
mov r9, [rbp + -152]
mov r10, [rbp + -184]
lea r11, [r9 + r10]
mov [rbp + -216], r11
mov r9, [rbp + -200]
mov r10, r9
mov [rbp + -296], r10
mov r9, [rbp + -216]
imul r10, r9
mov [rbp + -296], r10
mov r9, [rbp + -240]
mov r10, [rbp + -296]
lea r11, [r9 + r10]
mov [rbp + -304], r11
mov r9, [rbp + -288]
lea r10, [r9 + 8]
mov [rbp + -272], r10
mov r9, [rbp + -304]
mov r10, [rbp + -272]
mov [r9], r10
after:
mov r9, [rbp + -112]
lea r10, [r9 + 1]
mov [rbp + -232], r10
mov r9, [rbp + -232]
mov r10, r9
mov [rbp + -112], r10
mov r9, [rbp + -136]
cmp r10, r9
mov [rbp + -112], r10
setl al
mov r9, rax
mov [rbp + -40], r9
cmp r9, 1
mov [rbp + -40], r9
je header
mov r9, 0
mov [rbp + -112], r9
mov r9, [rbp + -96]
lea r10, [r9 + 1]
mov [rbp + -32], r10
mov r9, [rbp + -32]
mov r10, r9
mov [rbp + -96], r10
jmp header
done:
mov rsp, rbp
pop rbp
ret
