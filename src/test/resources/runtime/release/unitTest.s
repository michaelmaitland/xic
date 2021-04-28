.file "unitTest.xi"
.intel_syntax noprefix
.text
.globl _Imain_paai
.type _Imain_paai, @function
_Imain_paai:
push rbp
mov rbp, rsp
sub rsp, 160
mov r10, rdi
mov [rbp + -152], r10
mov r10, 104
mov [rbp + -120], r10
mov r10, [rbp + -56]
lea r10, [rsp + -8]
mov [rbp + -56], r10
mov r10, [rbp + -56]
push r10
sub rsp, 8
mov r10, [rbp + -120]
mov rdi, r10
call _xi_alloc
mov r10, rax
mov [rbp + -104], r10
add rsp, 16
mov r10, [rbp + -104]
mov QWORD PTR [r10], 12
mov r10, [rbp + -104]
mov r11, [rbp + -40]
lea r11, [r10 + 8]
mov [rbp + -40], r11
mov r10, [rbp + -40]
mov QWORD PTR [r10], 72
mov r10, [rbp + -104]
mov r11, [rbp + -8]
lea r11, [r10 + 16]
mov [rbp + -8], r11
mov r10, [rbp + -8]
mov QWORD PTR [r10], 101
mov r10, [rbp + -104]
mov r11, [rbp + -72]
lea r11, [r10 + 24]
mov [rbp + -72], r11
mov r10, [rbp + -72]
mov QWORD PTR [r10], 108
mov r10, [rbp + -104]
mov r11, [rbp + -64]
lea r11, [r10 + 32]
mov [rbp + -64], r11
mov r10, [rbp + -64]
mov QWORD PTR [r10], 108
mov r10, [rbp + -104]
mov r11, [rbp + -136]
lea r11, [r10 + 40]
mov [rbp + -136], r11
mov r10, [rbp + -136]
mov QWORD PTR [r10], 111
mov r10, [rbp + -104]
mov r11, [rbp + -128]
lea r11, [r10 + 48]
mov [rbp + -128], r11
mov r10, [rbp + -128]
mov QWORD PTR [r10], 32
mov r10, [rbp + -104]
mov r11, [rbp + -80]
lea r11, [r10 + 56]
mov [rbp + -80], r11
mov r10, [rbp + -80]
mov QWORD PTR [r10], 119
mov r10, [rbp + -104]
mov r11, [rbp + -32]
lea r11, [r10 + 64]
mov [rbp + -32], r11
mov r10, [rbp + -32]
mov QWORD PTR [r10], 111
mov r10, [rbp + -104]
mov r11, [rbp + -144]
lea r11, [r10 + 72]
mov [rbp + -144], r11
mov r10, [rbp + -144]
mov QWORD PTR [r10], 114
mov r10, [rbp + -104]
mov r11, [rbp + -96]
lea r11, [r10 + 80]
mov [rbp + -96], r11
mov r10, [rbp + -96]
mov QWORD PTR [r10], 108
mov r10, [rbp + -104]
mov r11, [rbp + -88]
lea r11, [r10 + 88]
mov [rbp + -88], r11
mov r10, [rbp + -88]
mov QWORD PTR [r10], 100
mov r10, [rbp + -104]
mov r11, [rbp + -48]
lea r11, [r10 + 96]
mov [rbp + -48], r11
mov r10, [rbp + -48]
mov QWORD PTR [r10], 33
mov r10, [rbp + -104]
mov r11, [rbp + -16]
lea r11, [r10 + 8]
mov [rbp + -16], r11
mov r10, [rbp + -16]
mov r11, r10
mov [rbp + -112], r11
mov r10, [rbp + -24]
lea r10, [rsp + -8]
mov [rbp + -24], r10
mov r10, [rbp + -24]
push r10
sub rsp, 8
mov r10, [rbp + -112]
mov rdi, r10
call _Iprint_pai
add rsp, 16
mov rsp, rbp
pop rbp
ret
_I$allocLayer_piiiiii:
push rbp
mov rbp, rsp
sub rsp, 320
mov r10, rdi
mov [rbp + -192], r10
mov r10, rsi
mov [rbp + -176], r10
mov r10, rdx
mov [rbp + -288], r10
mov r10, rcx
mov [rbp + -168], r10
mov r10, r8
mov [rbp + -112], r10
mov r10, r9
mov [rbp + -104], r10
mov r10, 0
mov [rbp + -256], r10
mov r10, 0
mov [rbp + -272], r10
mov r10, [rbp + -192]
mov r11, [rbp + -136]
lea r11, [r10 + 1]
mov [rbp + -136], r11
mov r10, [rbp + -136]
mov r11, r10
mov [rbp + -304], r11
header:
mov r10, [rbp + -272]
mov r11, r10
mov [rbp + -16], r11
mov r10, [rbp + -304]
mov r11, [rbp + -16]
imul r11, r10
mov [rbp + -16], r11
mov r10, [rbp + -256]
mov r11, [rbp + -16]
mov r12, [rbp + -56]
lea r12, [r10 + r11]
mov [rbp + -56], r12
mov r10, [rbp + -56]
mov r11, r10
mov [rbp + -264], r11
xor rax, rax
mov r10, [rbp + -288]
mov r11, [rbp + -264]
cmp r11, r10
mov [rbp + -264], r11
setl al
mov r10, rax
mov [rbp + -80], r10
mov r10, 1
mov [rbp + -64], r10
mov r10, [rbp + -80]
mov r11, r10
mov [rbp + -96], r11
mov r10, [rbp + -64]
mov r11, [rbp + -96]
xor r11, r10
mov [rbp + -96], r11
mov r10, [rbp + -96]
cmp r10, 1
mov [rbp + -96], r10
je done
mov r10, 0
mov [rbp + -8], r10
xor rax, rax
mov r10, [rbp + -8]
mov r11, [rbp + -256]
cmp r11, r10
mov [rbp + -256], r11
sete al
mov r10, rax
mov [rbp + -312], r10
mov r10, [rbp + -312]
cmp r10, 1
mov [rbp + -312], r10
je after
mov r10, [rbp + -176]
mov r11, [rbp + -216]
lea r11, [r10 + 1]
mov [rbp + -216], r11
mov r10, [rbp + -192]
mov r11, r10
mov [rbp + -208], r11
mov r10, [rbp + -216]
mov r11, [rbp + -208]
imul r11, r10
mov [rbp + -208], r11
mov r10, [rbp + -272]
mov r11, r10
mov [rbp + -240], r11
mov r10, [rbp + -208]
mov r11, [rbp + -240]
imul r11, r10
mov [rbp + -240], r11
mov r10, 1
mov [rbp + -224], r10
mov r10, [rbp + -256]
mov r11, r10
mov [rbp + -248], r11
mov r10, [rbp + -224]
mov r11, [rbp + -248]
sub r11, r10
mov [rbp + -248], r11
mov r10, [rbp + -176]
mov r11, [rbp + -152]
lea r11, [r10 + 1]
mov [rbp + -152], r11
mov r10, [rbp + -248]
mov r11, r10
mov [rbp + -144], r11
mov r10, [rbp + -152]
mov r11, [rbp + -144]
imul r11, r10
mov [rbp + -144], r11
mov r10, [rbp + -144]
mov r11, [rbp + -112]
mov r12, [rbp + -32]
lea r12, [r10 + r11]
mov [rbp + -32], r12
mov r10, [rbp + -240]
mov r11, [rbp + -32]
mov r12, [rbp + -24]
lea r12, [r10 + r11]
mov [rbp + -24], r12
mov r10, [rbp + -24]
mov r11, r10
mov [rbp + -128], r11
mov r10, 8
mov [rbp + -48], r10
mov r10, [rbp + -48]
mov r11, r10
mov [rbp + -88], r11
mov r10, [rbp + -128]
mov r11, [rbp + -88]
imul r11, r10
mov [rbp + -88], r11
mov r10, [rbp + -104]
mov r11, [rbp + -88]
mov r12, [rbp + -72]
lea r12, [r10 + r11]
mov [rbp + -72], r12
mov r10, [rbp + -72]
mov r11, r10
mov [rbp + -120], r11
mov r10, [rbp + -120]
mov r11, [rbp + -176]
mov [r10], r11
mov r10, 8
mov [rbp + -320], r10
mov r10, [rbp + -264]
mov r11, [rbp + -168]
mov r12, [rbp + -296]
lea r12, [r10 + r11]
mov [rbp + -296], r12
mov r10, [rbp + -320]
mov r11, r10
mov [rbp + -200], r11
mov r10, [rbp + -296]
mov r11, [rbp + -200]
imul r11, r10
mov [rbp + -200], r11
mov r10, [rbp + -104]
mov r11, [rbp + -200]
mov r12, [rbp + -184]
lea r12, [r10 + r11]
mov [rbp + -184], r12
mov r10, [rbp + -120]
mov r11, [rbp + -232]
lea r11, [r10 + 8]
mov [rbp + -232], r11
mov r10, [rbp + -184]
mov r11, [rbp + -232]
mov [r10], r11
after:
mov r10, [rbp + -256]
mov r11, [rbp + -160]
lea r11, [r10 + 1]
mov [rbp + -160], r11
mov r10, [rbp + -160]
mov r11, r10
mov [rbp + -256], r11
xor rax, rax
mov r10, [rbp + -304]
mov r11, [rbp + -256]
cmp r11, r10
mov [rbp + -256], r11
setl al
mov r10, rax
mov [rbp + -40], r10
mov r10, [rbp + -40]
cmp r10, 1
mov [rbp + -40], r10
je header
mov r10, 0
mov [rbp + -256], r10
mov r10, [rbp + -272]
mov r11, [rbp + -280]
lea r11, [r10 + 1]
mov [rbp + -280], r11
mov r10, [rbp + -280]
mov r11, r10
mov [rbp + -272], r11
jmp header
done:
mov rsp, rbp
pop rbp
ret
