#include<stdio.h>
#include<string.h>
#include<malloc.h>
#define TRUE 1
#define FALSE 0
#define OK 1
#define ERROR 0

typedef int Status;

typedef struct
{
	char data;
	unsigned int weight;
	unsigned int parent,lchild,rchild;
}HuffNode,*HuffmanTree;

typedef char **HuffmanCode;

void Select(HuffmanTree HT,int n,int &s1,int &s2)
{
	int i,min,min1,min2; min=9999;
	for(i=1;i<=n;i++)
	{
		if(HT[i].parent==0)
		if(HT[i].weight<=min) 
		{
			min=HT[i].weight;
			min1=i;
		}
	}
	min=9999;
	for(i=1;i<=n;i++)
	{
		if(i!=min1)
		if(HT[i].parent==0) 
		if(HT[i].weight<=min) 
		{
			min=HT[i].weight;
			min2=i;
		}
	}
	s1=min1; s2=min2;
}

Status CreateHuffmanTree(HuffmanTree &HT,int n)
{
	int m,i,s1,s2;
	if(n<=1) return ERROR;
	m=n*2-1;
	HT=(HuffmanTree)malloc((m+1)*sizeof(HuffNode));
	for(i=1;i<=m;i++)
	{
		HT[i].parent=0; HT[i].lchild=0; HT[i].rchild=0;
	}
	printf("Please input data information and weight values in sequence:\n");
	for(i=1;i<=n;i++)
	{
		scanf("%c%d",&HT[i].data,&HT[i].weight);
		getchar();
		printf("%dth input completed\n",i);
	}
	for(i=n+1;i<=m;i++)
	{
		Select(HT,i-1,s1,s2);
		HT[s1].parent=i; HT[s2].parent=i;
		HT[i].lchild=s1; HT[i].rchild=s2;
		HT[i].weight=HT[s1].weight+HT[s2].weight;
	}
	printf("HuffmanTree created successfully!\n");
	return OK;
}

void HuffmanCoding(HuffmanTree HT,HuffmanCode &HC,int n)
{
	int i,c,f,start;
	char *cd;
	HC=(HuffmanCode)malloc((n+1)*sizeof(char*));
	cd=(char*)malloc(n*sizeof(char));
	cd[n-1]='\0';
	for(i=1;i<=n;i++)
	{
		start=n-1; c=i; f=HT[i].parent;
		while(f!=0)
		{
			start--;
			if(HT[f].lchild==c) cd[start]='0';
			if(HT[f].rchild==c) cd[start]='1';
			c=f; f=HT[f].parent;
		}
		HC[i]=(char*)malloc((n-start)*sizeof(char));
		strcpy(HC[i],&cd[start]);
	}
	printf("HuffmanTree coded successfully!\n");
}

void StringCoding(HuffmanTree HT,HuffmanCode HC,int n)
{
	int i,j,length;
	char s[100];
	printf("Please enter the string you want for coding:\n");
	gets(s);
	printf("The coding of this string is:");
	length=strlen(s);
	for(i=0;i<length;i++)
	{
		for(j=1;j<=n;j++)
			if(s[i]==HT[j].data)
			printf("%s",HC[j]);
	}
	printf("\n");
}

void HuffmanDecoding(HuffmanTree HT,int n)
{
	int i,m,length,root,temp;
	char code[100];
	m=n*2-1;
	printf("Please enter the encoding you want for decoding:\n");
	gets(code);
	printf("The decoding of this encoding is:");
	length=strlen(code);
	for(i=1;i<=m;i++)
	if(HT[i].parent==0) root=i;
	temp=root;
	for(i=0;i<length;i++)
	{
		if(code[i]=='0') temp=HT[temp].lchild;
		else if(code[i]=='1') temp=HT[temp].rchild;
		if(HT[temp].lchild==0&&HT[temp].rchild==0)
		{
			printf("%c",HT[temp].data);
			temp=root;
		}
	}
	printf("\n");
}

int main()
{
	int x,i,n,s1,s2;
	HuffmanTree HT;
	HuffmanCode HC;
	printf("DIRECTORY:\n1.Input data and establish HuffmanTree\n2.View node information of the HuffmanTree\n3.Encoding for generating HuffmanTree\n4.Input string generating encoding\n5.Input encoding generating decoding\n0.End Program\n");
	while(x!=0)
	{
		printf("\nPlease enter the number of the function you want to use:\n");
	    scanf("%d",&x);
	    getchar();
	    switch(x)
	    {
	    	case 1:
	    		printf("Please enter the number of data information:\n");
	            scanf("%d",&n);
	            getchar();
	            CreateHuffmanTree(HT,n);
	            break;
	        case 2:
	        	printf("data weight parent lchild rchild\n");
	        	for(i=1;i<=n*2-1;i++)
	            printf("  %c    %d    %d    %d    %d\n",HT[i].data,HT[i].weight,HT[i].parent,HT[i].lchild,HT[i].rchild);
	            break;
			case 3:
				HuffmanCoding(HT,HC,n);
				printf("The coding of the HuffmanTree is:\n");
				for(i=1;i<=n;i++)
	            printf("%c--%s\n",HT[i].data,HC[i]);
	            break;
	        case 4:
	        	StringCoding(HT,HC,n);
	        	break;
	        case 5:
	        	HuffmanDecoding(HT,n);
	        	break;
	        case 0:
	        	break;
		}
	}
	printf("PROGRAM END\n");
	return 0;
}