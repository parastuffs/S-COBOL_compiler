#include <stdio.h>

int a;
int b;
int c;

int main() {
	scanf("%d",&a);
	scanf("%d",&b);

	if(a == b) {
		a = b+1;
	}
	else if(a>b) {
		a = b-1;
	}
	else {
		a = 0;
	}

	if(b == 5) {
		b = 1;
	}
}
