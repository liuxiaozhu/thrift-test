namespace java me.islim.thrift.zk
service EchoSerivce
{
	string echo(1: string msg);
}