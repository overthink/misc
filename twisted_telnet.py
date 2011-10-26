#!/usr/bin/python
# vim: sw=4 ts=4
import sys
import base64
from twisted.internet import reactor
from twisted.internet.protocol import ClientCreator, Protocol

URL=sys.argv[1]
PROXY_HOST=sys.argv[2]
PROXY_PORT=int(sys.argv[3])
PROXY_USER=sys.argv[4]
PROXY_PW=sys.argv[5]

def make_proxy_auth_header(user, pw):
    return "Proxy-Authorization: Basic {0}\n".format(base64.b64encode("{0}:{1}".format(user, pw)))

class ProxyCheckProtocol(Protocol):
    def connectionMade(self):
        self.transport.write("GET {0} HTTP/1.1\n".format(URL))
        self.transport.write("User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.4 Safari/535.7\n")
        self.transport.write(make_proxy_auth_header(PROXY_USER, PROXY_PW))
        self.transport.write("\n")

    def dataReceived(self, data):
        # Bear in mind 'data' may only be a single packet's worth of data, not
        # the whole result.
        for line in data.splitlines():
            if line.startswith("HTTP/1"):
                # found status line
                status_code = line.split(" ")[1]
                print "Status: {0}".format(status_code)
                self.transport.loseConnection()
                reactor.stop()

c = ClientCreator(reactor, ProxyCheckProtocol)
c.connectTCP(PROXY_HOST, PROXY_PORT) # returns a Deferred
reactor.run()

