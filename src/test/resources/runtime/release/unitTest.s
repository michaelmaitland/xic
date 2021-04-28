.file "unitTest.xi"
.intel_syntax noprefix
.text
.globl _Imain_paai
.type _Imain_paai, @function
_Imain_paai:
push rbp
mov rbp, rsp
sub rsp, 112
mov r9, rdi
mov [rbp + -96], r9
mov r9, 1
mov [rbp + -32], r9
mov r9, 1
mov [rbp + -48], r9
mov r9, [rbp + -40]
lea r9, [rsp + -8]
mov [rbp + -40], r9
mov r9, [rbp + -40]
push r9
sub rsp, 8
mov r9, [rbp + -32]
mov rdi, r9
mov r9, [rbp + -48]
mov rsi, r9
call _Ibooly_pbb
add rsp, 16
mov r9, 1
mov [rbp + -56], r9
mov r9, 0
mov [rbp + -8], r9
mov r9, [rbp + -72]
lea r9, [rsp + -8]
mov [rbp + -72], r9
mov r9, [rbp + -72]
push r9
sub rsp, 8
mov r9, [rbp + -56]
mov rdi, r9
mov r9, [rbp + -8]
mov rsi, r9
call _Ibooly_pbb
add rsp, 16
mov r9, 0
mov [rbp + -16], r9
mov r9, 1
mov [rbp + -104], r9
mov r9, [rbp + -88]
lea r9, [rsp + -8]
mov [rbp + -88], r9
mov r9, [rbp + -88]
push r9
sub rsp, 8
mov r9, [rbp + -16]
mov rdi, r9
mov r9, [rbp + -104]
mov rsi, r9
call _Ibooly_pbb
add rsp, 16
mov r9, 0
mov [rbp + -64], r9
mov r9, 0
mov [rbp + -80], r9
mov r9, [rbp + -24]
lea r9, [rsp + -8]
mov [rbp + -24], r9
mov r9, [rbp + -24]
push r9
sub rsp, 8
mov r9, [rbp + -64]
mov rdi, r9
mov r9, [rbp + -80]
mov rsi, r9
call _Ibooly_pbb
add rsp, 16
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
mov [rbp + -184], r9
mov r9, rdx
mov [rbp + -176], r9
mov r9, rcx
mov [rbp + -256], r9
mov r9, r8
mov [rbp + -272], r9
mov r9, r9
mov [rbp + -280], r9
mov r9, 0
mov [rbp + -128], r9
mov r9, 0
mov [rbp + -112], r9
mov r9, [rbp + -168]
mov r10, [rbp + -8]
lea r10, [r9 + 1]
mov [rbp + -8], r10
mov r9, [rbp + -8]
mov r10, r9
mov [rbp + -144], r10
header:
mov r9, [rbp + -112]
mov r10, r9
mov [rbp + -16], r10
mov r9, [rbp + -144]
mov r10, [rbp + -16]
imul r10, r9
mov [rbp + -16], r10
mov r9, [rbp + -128]
mov r10, [rbp + -16]
mov r11, [rbp + -104]
lea r11, [r9 + r10]
mov [rbp + -104], r11
mov r9, [rbp + -104]
mov r10, r9
mov [rbp + -160], r10
xor rax, rax
mov r9, [rbp + -176]
mov r10, [rbp + -160]
cmp r10, r9
mov [rbp + -160], r10
setl al
mov r9, rax
mov [rbp + -120], r9
mov r9, 1
mov [rbp + -136], r9
mov r9, [rbp + -120]
mov r10, r9
mov [rbp + -88], r10
mov r9, [rbp + -136]
mov r10, [rbp + -88]
xor r10, r9
mov [rbp + -88], r10
mov r9, [rbp + -88]
cmp r9, 1
mov [rbp + -88], r9
je done
mov r9, 0
mov [rbp + -96], r9
xor rax, rax
mov r9, [rbp + -96]
mov r10, [rbp + -128]
cmp r10, r9
mov [rbp + -128], r10
sete al
mov r9, rax
mov [rbp + -152], r9
mov r9, [rbp + -152]
cmp r9, 1
mov [rbp + -152], r9
je after
mov r9, [rbp + -184]
mov r10, [rbp + -296]
lea r10, [r9 + 1]
mov [rbp + -296], r10
mov r9, [rbp + -168]
mov r10, r9
mov [rbp + -304], r10
mov r9, [rbp + -296]
mov r10, [rbp + -304]
imul r10, r9
mov [rbp + -304], r10
mov r9, [rbp + -112]
mov r10, r9
mov [rbp + -312], r10
mov r9, [rbp + -304]
mov r10, [rbp + -312]
imul r10, r9
mov [rbp + -312], r10
mov r9, 1
mov [rbp + -320], r9
mov r9, [rbp + -128]
mov r10, r9
mov [rbp + -264], r10
mov r9, [rbp + -320]
mov r10, [rbp + -264]
sub r10, r9
mov [rbp + -264], r10
mov r9, [rbp + -184]
mov r10, [rbp + -192]
lea r10, [r9 + 1]
mov [rbp + -192], r10
mov r9, [rbp + -264]
mov r10, r9
mov [rbp + -208], r10
mov r9, [rbp + -192]
mov r10, [rbp + -208]
imul r10, r9
mov [rbp + -208], r10
mov r9, [rbp + -208]
mov r10, [rbp + -272]
mov r11, [rbp + -216]
lea r11, [r9 + r10]
mov [rbp + -216], r11
mov r9, [rbp + -312]
mov r10, [rbp + -216]
mov r11, [rbp + -56]
lea r11, [r9 + r10]
mov [rbp + -56], r11
mov r9, [rbp + -56]
mov r10, r9
mov [rbp + -288], r10
mov r9, 8
mov [rbp + -64], r9
mov r9, [rbp + -64]
mov r10, r9
mov [rbp + -72], r10
mov r9, [rbp + -288]
mov r10, [rbp + -72]
imul r10, r9
mov [rbp + -72], r10
mov r9, [rbp + -280]
mov r10, [rbp + -72]
mov r11, [rbp + -80]
lea r11, [r9 + r10]
mov [rbp + -80], r11
mov r9, [rbp + -80]
mov r10, r9
mov [rbp + -240], r10
mov r9, [rbp + -240]
mov r10, [rbp + -184]
mov [r9], r10
mov r9, 8
mov [rbp + -32], r9
mov r9, [rbp + -160]
mov r10, [rbp + -256]
mov r11, [rbp + -40]
lea r11, [r9 + r10]
mov [rbp + -40], r11
mov r9, [rbp + -32]
mov r10, r9
mov [rbp + -48], r10
mov r9, [rbp + -40]
mov r10, [rbp + -48]
imul r10, r9
mov [rbp + -48], r10
mov r9, [rbp + -280]
mov r10, [rbp + -48]
mov r11, [rbp + -232]
lea r11, [r9 + r10]
mov [rbp + -232], r11
mov r9, [rbp + -240]
mov r10, [rbp + -248]
lea r10, [r9 + 8]
mov [rbp + -248], r10
mov r9, [rbp + -232]
mov r10, [rbp + -248]
mov [r9], r10
after:
mov r9, [rbp + -128]
mov r10, [rbp + -200]
lea r10, [r9 + 1]
mov [rbp + -200], r10
mov r9, [rbp + -200]
mov r10, r9
mov [rbp + -128], r10
xor rax, rax
mov r9, [rbp + -144]
mov r10, [rbp + -128]
cmp r10, r9
mov [rbp + -128], r10
setl al
mov r9, rax
mov [rbp + -224], r9
mov r9, [rbp + -224]
cmp r9, 1
mov [rbp + -224], r9
je header
mov r9, 0
mov [rbp + -128], r9
mov r9, [rbp + -112]
mov r10, [rbp + -24]
lea r10, [r9 + 1]
mov [rbp + -24], r10
mov r9, [rbp + -24]
mov r10, r9
mov [rbp + -112], r10
jmp header
done:
mov rsp, rbp
pop rbp
ret
_Ibooly_pbb:
push rbp
mov rbp, rsp
sub rsp, 224
mov r9, rdi
mov [rbp + -136], r9
mov r9, rsi
mov [rbp + -152], r9
mov r9, 1
mov [rbp + -216], r9
mov r9, [rbp + -136]
mov r10, r9
mov [rbp + -144], r10
mov r9, [rbp + -216]
mov r10, [rbp + -144]
xor r10, r9
mov [rbp + -144], r10
mov r9, [rbp + -144]
cmp r9, 1
mov [rbp + -144], r9
je _l5
mov r9, 16
mov [rbp + -88], r9
mov r9, [rbp + -192]
lea r9, [rsp + -8]
mov [rbp + -192], r9
mov r9, [rbp + -192]
push r9
sub rsp, 8
mov r9, [rbp + -88]
mov rdi, r9
call _xi_alloc
mov r9, rax
mov [rbp + -96], r9
add rsp, 16
mov r9, [rbp + -96]
mov QWORD PTR [r9], 1
mov r9, [rbp + -96]
mov r10, [rbp + -24]
lea r10, [r9 + 8]
mov [rbp + -24], r10
mov r9, [rbp + -24]
mov QWORD PTR [r9], 49
mov r9, [rbp + -96]
mov r10, [rbp + -128]
lea r10, [r9 + 8]
mov [rbp + -128], r10
mov r9, [rbp + -128]
mov r10, r9
mov [rbp + -104], r10
mov r9, [rbp + -160]
lea r9, [rsp + -8]
mov [rbp + -160], r9
mov r9, [rbp + -160]
push r9
sub rsp, 8
mov r9, [rbp + -104]
mov rdi, r9
call _Iprint_pai
add rsp, 16
_l6:
mov rsp, rbp
pop rbp
ret
_l5:
mov r9, 1
mov [rbp + -168], r9
mov r9, [rbp + -152]
mov r10, r9
mov [rbp + -184], r10
mov r9, [rbp + -168]
mov r10, [rbp + -184]
xor r10, r9
mov [rbp + -184], r10
mov r9, [rbp + -184]
cmp r9, 1
mov [rbp + -184], r9
je _l2
mov r9, 16
mov [rbp + -48], r9
mov r9, [rbp + -8]
lea r9, [rsp + -8]
mov [rbp + -8], r9
mov r9, [rbp + -8]
push r9
sub rsp, 8
mov r9, [rbp + -48]
mov rdi, r9
call _xi_alloc
mov r9, rax
mov [rbp + -120], r9
add rsp, 16
mov r9, [rbp + -120]
mov QWORD PTR [r9], 1
mov r9, [rbp + -120]
mov r10, [rbp + -40]
lea r10, [r9 + 8]
mov [rbp + -40], r10
mov r9, [rbp + -40]
mov QWORD PTR [r9], 50
mov r9, [rbp + -120]
mov r10, [rbp + -176]
lea r10, [r9 + 8]
mov [rbp + -176], r10
mov r9, [rbp + -176]
mov r10, r9
mov [rbp + -56], r10
mov r9, [rbp + -200]
lea r9, [rsp + -8]
mov [rbp + -200], r9
mov r9, [rbp + -200]
push r9
sub rsp, 8
mov r9, [rbp + -56]
mov rdi, r9
call _Iprint_pai
add rsp, 16
_l3:
jmp _l6
_l2:
mov r9, 16
mov [rbp + -64], r9
mov r9, [rbp + -16]
lea r9, [rsp + -8]
mov [rbp + -16], r9
mov r9, [rbp + -16]
push r9
sub rsp, 8
mov r9, [rbp + -64]
mov rdi, r9
call _xi_alloc
mov r9, rax
mov [rbp + -112], r9
add rsp, 16
mov r9, [rbp + -112]
mov QWORD PTR [r9], 1
mov r9, [rbp + -112]
mov r10, [rbp + -208]
lea r10, [r9 + 8]
mov [rbp + -208], r10
mov r9, [rbp + -208]
mov QWORD PTR [r9], 51
mov r9, [rbp + -112]
mov r10, [rbp + -72]
lea r10, [r9 + 8]
mov [rbp + -72], r10
mov r9, [rbp + -72]
mov r10, r9
mov [rbp + -80], r10
mov r9, [rbp + -32]
lea r9, [rsp + -8]
mov [rbp + -32], r9
mov r9, [rbp + -32]
push r9
sub rsp, 8
mov r9, [rbp + -80]
mov rdi, r9
call _Iprint_pai
add rsp, 16
jmp _l3
