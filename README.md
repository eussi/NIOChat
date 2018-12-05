# NIOChat
use NIO to finish a chat
## usage:
1. run top.eussi.bootstrap.ServerStartup
> you can see : Service launched，The listening port is ：9999
2. run top.eussi.bootstrap.ClientStartup
> you can see : Please enter nickname
> then input your nickname like 'test1', you can see : welcometest1 enter the chat room! Current number of people online :1
3. run top.eussi.bootstrap.ClientStartup again
> you need input your nickname as well. now, two clients can receive the same thing, like 'welcomett enter the chat room! Current number of people online :2'
4. now you can run ClientStartup more, then Clients can chat with each other
