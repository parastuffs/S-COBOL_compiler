










@a = i18 0
@b = i18
@c = i21
@d = i5
define void @main()
	entry:
%res = load i32* @a
%digit = alloca i32
store i32 0 , i32 * %res
br label %read

%res = load i32* @b
%digit = alloca i32
store i32 0 , i32 * %res
br label %read

br label %ifThen.0

call i32 @putchar(i32 ')
call i32 @putchar(i32 v)
call i32 @putchar(i32 a)
call i32 @putchar(i32 l)
call i32 @putchar(i32 e)
call i32 @putchar(i32 u)
call i32 @putchar(i32 r)
call i32 @putchar(i32 :)
call i32 @putchar(i32 ')


call i32 @putchar(i32 0)



	find:
%16 = load i32* @b
%17 = load i32* @b
store i32 %17, i32 %16

br label %ifThen.1

%18 = load i32* @a
%19 = load i32* @b
store i32 %19, i32 %18

%20 = load i32* @c
%21 = load i32* @a
store i32 %21, i32 %20

	diff:
%22 = load i32* @b
%23 = load i32* @a
%24 = sub i32 %23, %22


read:
%0 = call i32 @getchar ()
%1 = sub i32 %0, 48
store i32 %1 , i32 * %digit
%2 = icmp ne i32 %0 , 10
br i1 %2 , label %save , label %exit
save:
%3 = load i32 * %res
%4 = load i32 * %digit
%5 = mul i32 %3 , 10
%6 = add i32 %5 , %4
store i32 %6 , i32 * %res
br label %read
exit:
%7 = load i32 * %res
ret i32 %7
read:
%8 = call i32 @getchar ()
%9 = sub i32 %8, 48
store i32 %9 , i32 * %digit
%10 = icmp ne i32 %8 , 10
br i1 %10 , label %save , label %exit
save:
%11 = load i32 * %res
%12 = load i32 * %digit
%13 = mul i32 %11 , 10
%14 = add i32 %13 , %12
store i32 %14 , i32 * %res
br label %read
exit:
%15 = load i32 * %res
ret i32 %15
	ifthen.0:br label %find

%cond = icmp eq i32 0, b
br i1 %cond, , label %ifthen.0
	ifthen.1:br label %diff

%cond = icmp slt i32 b, a
br i1 %cond, , label %ifthen.1
