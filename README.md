# mailhttp

A temp email box (no user authorization system) for private use.
HTTP API and simple UI support.


```json
GET http://{host}:9661/receive?address={email_address}

{
  success: Boolean # If there's a email received for this address in last 10 minute
  email: {         # only store one email for each address, if there's no email, this field will be null
    title: String,
    from: String,
    content: String,
    remoteAddress: String, # email sender's IP
  }
}
```

visit http://{host}:9661/ for simple UI.


## Deploy:

Resolve the domain name (A, MX) to the server correctly, and ensure that port 25, 9661 are open  
Run .jar in background (require jdk8)

