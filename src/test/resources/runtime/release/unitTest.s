.file "unitTest.xi"
.intel_syntax noprefix
.text
.globl _Imain_paai
.type _Imain_paai, @function
_Imain_paai:
push rbp
mov rbp, rsp
sub rsp, 64
mov r9, [rbp + -56]
mov r9, rdi
mov r9, [rbp + -48]
mov r9, 16
mov r9, [rbp + -64]
lea r9, [rsp + -8]
mov [rbp + -64], r9
mov r9, [rbp + -64]
push r9
mov r9, [rbp + -48]
mov rdi, r9
call _xi_alloc
mov r9, [rbp + -40]
mov r9, rax
add rsp, 8
mov r9, [rbp + -40]
mov [r9], 1
mov r9, [rbp + -32]
mov r10, [rbp + -40]
lea r9, [r10 + 8]
mov [rbp + -32], r9
mov r9, [rbp + -32]
mov [r9], 97
mov r9, [rbp + -8]
mov r10, [rbp + -40]
lea r9, [r10 + 8]
mov [rbp + -8], r9
mov r9, [rbp + -16]
mov r10, [rbp + -8]
mov r9, r10
mov r9, [rbp + -24]
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
mov r9, [rbp + -168]
mov r9, rdi
mov r9, [rbp + -208]
mov r9, rsi
mov r9, [rbp + -176]
mov r9, rdx
mov r9, [rbp + -184]
mov r9, rcx
mov r9, [rbp + -224]
mov r9, r8
mov r9, [rbp + -240]
mov r9, r9
mov r9, [rbp + -112]
mov r9, 0
mov r9, [rbp + -96]
mov r9, 0
mov r9, [rbp + -16]
mov r10, [rbp + -168]
lea r9, [r10 + 1]
mov [rbp + -16], r9
mov r9, [rbp + -136]
mov r10, [rbp + -16]
mov r9, r10
header:
mov r9, [rbp + -120]
mov r10, [rbp + -96]
mov r9, r10
mov r9, [rbp + -120]
mov r10, [rbp + -136]
imul r9, r10
mov [rbp + -120], r9
mov r9, [rbp + -144]
mov r10, [rbp + -112]
mov r11, [rbp + -120]
lea r9, [r10 + r11]
mov [rbp + -144], r9
mov r9, [rbp + -152]
mov r10, [rbp + -144]
mov r9, r10
mov r9, [rbp + -152]
mov r10, [rbp + -176]
cmp r9, r10
mov [rbp + -152], r9
setl al
mov r9, [rbp + -56]
mov r9, rax
mov r9, [rbp + -72]
mov r9, 1
mov r9, [rbp + -88]
mov r10, [rbp + -56]
mov r9, r10
mov r9, [rbp + -88]
mov r10, [rbp + -72]
xor r9, r10
mov [rbp + -88], r9
mov r9, [rbp + -88]
cmp r9, 1
mov [rbp + -88], r9
je done
mov r9, [rbp + -48]
mov r9, 0
mov r9, [rbp + -112]
mov r10, [rbp + -48]
cmp r9, r10
mov [rbp + -112], r9
sete al
mov r9, [rbp + -192]
mov r9, rax
mov r9, [rbp + -192]
cmp r9, 1
mov [rbp + -192], r9
je after
mov r9, [rbp + -312]
mov r10, [rbp + -208]
lea r9, [r10 + 1]
mov [rbp + -312], r9
mov r9, [rbp + -320]
mov r10, [rbp + -168]
mov r9, r10
mov r9, [rbp + -320]
mov r10, [rbp + -312]
imul r9, r10
mov [rbp + -320], r9
mov r9, [rbp + -248]
mov r10, [rbp + -96]
mov r9, r10
mov r9, [rbp + -248]
mov r10, [rbp + -320]
imul r9, r10
mov [rbp + -248], r9
mov r9, [rbp + -256]
mov r9, 1
mov r9, [rbp + -280]
mov r10, [rbp + -112]
mov r9, r10
mov r9, [rbp + -280]
mov r10, [rbp + -256]
sub r9, r10
mov [rbp + -280], r9
mov r9, [rbp + -8]
mov r10, [rbp + -208]
lea r9, [r10 + 1]
mov [rbp + -8], r9
mov r9, [rbp + -24]
mov r10, [rbp + -280]
mov r9, r10
mov r9, [rbp + -24]
mov r10, [rbp + -8]
imul r9, r10
mov [rbp + -24], r9
mov r9, [rbp + -104]
mov r10, [rbp + -24]
mov r11, [rbp + -224]
lea r9, [r10 + r11]
mov [rbp + -104], r9
mov r9, [rbp + -128]
mov r10, [rbp + -248]
mov r11, [rbp + -104]
lea r9, [r10 + r11]
mov [rbp + -128], r9
mov r9, [rbp + -264]
mov r10, [rbp + -128]
mov r9, r10
mov r9, [rbp + -160]
mov r9, 8
mov r9, [rbp + -64]
mov r10, [rbp + -160]
mov r9, r10
mov r9, [rbp + -64]
mov r10, [rbp + -264]
imul r9, r10
mov [rbp + -64], r9
mov r9, [rbp + -80]
mov r10, [rbp + -240]
mov r11, [rbp + -64]
lea r9, [r10 + r11]
mov [rbp + -80], r9
mov r9, [rbp + -288]
mov r10, [rbp + -80]
mov r9, r10
mov r9, [rbp + -288]
mov r10, [rbp + -208]
mov [r9], r10
mov r9, [rbp + -200]
mov r9, 8
mov r9, [rbp + -216]
mov r10, [rbp + -152]
mov r11, [rbp + -184]
lea r9, [r10 + r11]
mov [rbp + -216], r9
mov r9, [rbp + -296]
mov r10, [rbp + -200]
mov r9, r10
mov r9, [rbp + -296]
mov r10, [rbp + -216]
imul r9, r10
mov [rbp + -296], r9
mov r9, [rbp + -304]
mov r10, [rbp + -240]
mov r11, [rbp + -296]
lea r9, [r10 + r11]
mov [rbp + -304], r9
mov r9, [rbp + -272]
mov r10, [rbp + -288]
lea r9, [r10 + 8]
mov [rbp + -272], r9
mov r9, [rbp + -304]
mov r10, [rbp + -272]
mov [r9], r10
after:
mov r9, [rbp + -232]
mov r10, [rbp + -112]
lea r9, [r10 + 1]
mov [rbp + -232], r9
mov r9, [rbp + -112]
mov r10, [rbp + -232]
mov r9, r10
mov r9, [rbp + -112]
mov r10, [rbp + -136]
cmp r9, r10
mov [rbp + -112], r9
setl al
mov r9, [rbp + -40]
mov r9, rax
mov r9, [rbp + -40]
cmp r9, 1
mov [rbp + -40], r9
je header
mov r9, [rbp + -112]
mov r9, 0
mov r9, [rbp + -32]
mov r10, [rbp + -96]
lea r9, [r10 + 1]
mov [rbp + -32], r9
mov r9, [rbp + -96]
mov r10, [rbp + -32]
mov r9, r10
jmp header
done:
mov rsp, rbp
pop rbp
ret
