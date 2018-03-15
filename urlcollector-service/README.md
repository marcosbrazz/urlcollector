# URL-COLLECTOR

## What is it ?

It's an application based on microservices in Java to collect and store URLs found in web pages.

### How does it work ?

It's very simple:  Call the service </collect> providing a web page URL as parameter. The service will discover all the URLs inside the web page provided and store then in a database, then, for each URL discovered, the collector retrieve the web page, collect the URLs inside and store them.  

### Services

the services are accessible by this url template: http://<ip>:<port>/urlcollector/<resource>

#### Start a URL collector process.

This is an asynchronous process. The service only starts the collector and returns. The collector keeps running in a thread until there is no more URLs to visit or the user manually issues the stop by invoking the /stop service.
Each call of this service start a new collector process.  Multiple collector process may run in parallel.   

```bashp
POST /collect?url=<web_page_url>&depth=<integer>

*   url: a web page url 
*   depth (optional): A natural number. This parameter limits the depth level of URL visiting, starting from the base URL defined by the URL parameter.  

```

> IMPORTATNT: The depth parameters comes as an edge limit of the search space in depth for the URL discovery process. It also improves the process robustness by avoiding the process stack to grow indefinitely. So it's a recommend practice to provide the depth parameter.   

#### Stop a URL collector process

This service sign a stop request to a collector process. When the collector receives this request, the process stops in a safe manner, storing the URLs from the last page discovered.

 ```bashp
PUT /stop?url=<web_page_url>

*   url: a web page used to start a collector process.

```

#### Check processes status

```bashp
GET /status
Returns a list of the issued collector processes, listing the starting URL and a boolean indicating if the process is done (true). 
{
	"http://mussumipsum.com/":false,
	"http://www.pudim.com.br/":true
}

```

#### Get stored URLs

```bashp
GET /links?first=<integer>

*   first (optional): Return the first n URLs found in database. If omitted, all of the URLs stored in database are returned.  
[
    "http://diegoesteves.ink/en/distintos/",
    "http://diegoesteves.ink/wp/wp-content/themes/diego-esteves/style.css?v=1.0.4",
    "http://diegoesteves.ink/wp/wp-content/themes/diego-esteves/favicons/favicon-32x32.png",
    "http://diegoesteves.ink/en/displaytec/",
    "mailto:hello@diegoesteves.ink",
    "http://diegoesteves.ink/en/jobs/code",
]
```

## Build and Install

### Pre-Requisites:

1.  Create a cluster in [IBM® Cloud®](https://www.ibm.com/cloud/);
2.  Install [IBM Cloud CLI](https://console.bluemix.net/docs/cli/reference/bluemix_cli/get_started.html#getting-started);
3.  Install [Kubernetes CLI](https://kubernetes.io/docs/user-guide/prereqs/);
4.  [Created an API KEY](https://console.bluemix.net/docs/iam/login_fedid.html#federated_id);
5.  Create a Cloudant database service instance and setup an empty database.

### Prepare source
 
 In the source file:

*  Edit the file urlcollector-service.yaml. Replace the value of the property image to point to your image registry.
*  Edit the file application.yml to configure the Cloudant database parameters.
 
### Run build script 
 
*  Run build.sh

Run the build.sh as follows:
```bash
$ ./build.sh -n <registry_namespace> -k <api-key file> -r <region> -c <cluster-name>
```
## Access services on IBM Cloud Container

You can access your app publicly through your Cluster IP and the NodePort. The NodePort should be 30080.

* To find your IP:
```bash
$ bx cs workers <cluster-name>
ID                                                 Public IP        Private IP      Machine Type   State    Status   
kube-dal10-paac005a5fa6c44786b5dfb3ed8728548f-w1   169.47.241.213   10.177.155.13   free           normal   Ready  
```
* To find the NodePort of the account-summary service:
```bash
$ kubectl get svc
NAME                    CLUSTER-IP     EXTERNAL-IP   PORT(S)                                                                      AGE
...
account-summary         10.10.10.74    <nodes>       80:30080/TCP                                                                 2d
...
```
