










@a = i18 0
@b = i18
@c = i21
@d = i5
define void @main()
	entry:



%cond = icmp eq i32 0, b




	find:
%0 = load i32* @b
%1 = load i32* @b
store i32 %1, i32 %0


%cond = icmp slt i32 b, a

%2 = load i32* @a
%3 = load i32* @b
store i32 %3, i32 %2

%4 = load i32* @c
%5 = load i32* @a
store i32 %5, i32 %4

	diff:
%6 = load i32* @b
%7 = load i32* @a
%8 = sub i32 %7, %6

