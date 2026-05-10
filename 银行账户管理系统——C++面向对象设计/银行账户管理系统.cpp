#include<iostream>
#include<string>
using namespace std;
class Card
{
	private:
		int id;
		string name;
		int code;
		int money;
	public:
		Card(int x,string y,int z,int o);
		int monitor(int);
		void display();
		void deposit();
		void withdraw();
		void account();
		void reset();
		void logout();
};

Card::Card(int x,string y,int z,int o):id(x),name(y),code(z),money(o){}

int Card::monitor(int t)
{
	int i,x;
	if(code==0)
	{
		cout<<"The account has been locked!Please contact the staff."<<endl;
		return 0;
	}
	if(code==-1)
	{
		cout<<"The account does not exist!"<<endl;
		return 0;
	}
	cout<<"Please enter password:"<<endl;
	try
	{
		for(i=3;i>0;i--)
	    {
		    cin>>t;
		    if(t==code)
		    {
		       return 1;
			   break;
		    }
		    else
		    {
			   cout<<"Password error!please re-enter("<<i-1<<" enter times remain):"<<endl;
		    }
	    }
	    if(t!=code) throw t;
	}
	catch(int)
	{
		cout<<"The enter times has been used up, and the account has been locked."<<endl;
    	code=0;
	}
	return 0;
}

void Card::display()
{
	int t,m;
	m=Card::monitor(t);
	if(m==1)
	cout<<"id:"<<id<<endl<<"name:"<<name<<endl<<"money:"<<money<<endl;
}

void Card::deposit()
{
	int e,t,m;
	m=Card::monitor(t);
	if(m==1)
	{
	    cout<<"Please enter the amount of money you want to deposit:"<<endl;
	    cin>>e;
	    money=money+e;
	    cout<<"Your money depositted successfully!"<<endl<<"Your account currently remained:"<<money<<endl;
	}
}

void Card::withdraw()
{
	int e,t,m;
	m=Card::monitor(t);
	if(m==1)
	{
	    cout<<"Please enter the amount of money you want to withdraw:"<<endl;
	    cin>>e;
	    if(e>money)
	      cout<<"Error, withdrawal exceeds balance!"<<endl;
	    else
	    {
		    money=money-e;
		    cout<<"Your money withdrawed successfully!"<<endl<<"Your account currently remained:"<<money<<endl;
	    }
    }
}

void Card::account()
{
	string a;
	int b;
	cout<<"Please enter your name:"<<endl;
	cin>>a;
	cout<<"Please set your password(6 figure):"<<endl;
	cin>>b;
	name=a;
	code=b;
	cout<<"Account opened successfully"<<endl<<"Your id is:"<<id<<endl<<"Your account currently remained:"<<money<<endl;
}

void Card::reset()
{
	int t;
	cout<<"Please enter a new password:"<<endl;
	cin>>t;
	code=t;
	cout<<"Password reset successfully"<<endl;
}

void Card::logout()
{
	int t,m;
	m=Card::monitor(t);
	if(m==1)
	{
	    name="NULL";
	    code=-1;
	    money=0;
	    cout<<"Account logout successfully"<<endl;
	}
}

int main()
{
	int n,m,i,j;
	n=1; i=0;
	Card card[5]={
		Card(202401,"NULL",0,0),
		Card(202402,"NULL",0,0),
		Card(202403,"NULL",0,0),
		Card(202404,"NULL",0,0),
		Card(202405,"NULL",0,0),
	};
	cout<<"Directory:"<<endl;
	cout<<"1.Open an account"<<endl<<"2.Account inquiry"<<endl<<"3.Deposit"<<endl<<"4.Withdraw"<<endl<<"5.Reset password"<<endl<<"6.Logout"<<endl<<"0.END PROGRAM"<<endl;
	while(n!=0)
	{
		cout<<"Please enter the number of the function you want to use:"<<endl;
		cin>>n;
		switch(n)
		{
			case 1:
				card[i].account();
				i++;
				break;
			case 2:
				cout<<"Please enter your id:"<<endl;
				cin>>m;
				j=m-202401;
				if(j>=i)
				{
					cout<<"Error,the account does not exist!"<<endl;
					break;
				}
				card[j].display();
				break;
			case 3:
				cout<<"Please enter your id:"<<endl;
				cin>>m;
				j=m-202401;
				if(j>=i)
				{
					cout<<"Error,the account does not exist!"<<endl;
					break;
				}
				card[j].deposit();
				break;
			case 4:
				cout<<"Please enter your id:"<<endl;
				cin>>m;
				j=m-202401;
				if(j>=i)
				{
					cout<<"Error,the account does not exist!"<<endl;
					break;
				}
				card[j].withdraw();
				break;
			case 5:
				cout<<"Please enter your id:"<<endl;
				cin>>m;
				j=m-202401;
				if(j>=i)
				{
					cout<<"Error,the account does not exist!"<<endl;
					break;
				}
				card[j].reset();
				break;
			case 6:
				cout<<"Please enter your id:"<<endl;
				cin>>m;
				j=m-202401;
				if(j>=i)
				{
					cout<<"Error,the account does not exist!"<<endl;
					break;
				}
				card[j].logout();
				break;
		}
		cout<<endl<<endl;
	}
	cout<<"Program end"<<endl;
	return 0;
}