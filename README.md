# ms-async-netty
ms-async-netty

Asynchronouse HTTP Client using Netty

Async HTTP Client
 An asynchronous HTTP client using Netty 4.x or greater.  The distinguishing
feature of an asynchronous HTTP client is the ability to concurrently
execute multiple requests using a smaller number of threads than there
are requests.

Another distinguishing feature is that the normal pattern of use is
to provide a callback which will be called with the result of a response,
rather than waiting for the response to be synchronously executed.

The API here is inspired a bit by <a href="http://nodejs.org">Node.js</a>'
<code>http </code> module; it is designed to (mostly) avoid the 
Future pattern, which a number of similar libraries seem to be stuck on
(the point of async code is not <i>not</i> block; if an asynchronous
API is all about "here's a thing you can block on while we make an
asynchronous call" then it has failed).

For details and example usage, see 
HttpClient.
