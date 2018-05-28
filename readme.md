# Integrations with Spring Shell, GRPC, and Kubernetes

## Client

### Spring Shell
Josh Long https://www.youtube.com/watch?v=h6nMjjxJWjk

Running the client in k8s:
`kubectl run -i -t floo-client --image=gcr.io/talknerdy-one/github-chickenbane-floo-client:latest --restart=Never --rm`

### Official Kubernetes Client

https://github.com/kubernetes-client/java


### Running and port-forwarding the proxy

Build and run the client: `./floo`
(Spring Boot's `bootRun` + Spring Shell play oddly together)

Before using the official java kubernetes client library, I was port-forwarding like an animal:
```
# port-forward proxy
kubectl port-forward $(kubectl get pods -l app=floo-proxy -o=jsonpath='{.items[0].metadata.name}') 6565 &
pfpid=$!

# run the client
./client/build/libs/client.jar

# stop port-forwarding
trap "kill $pfpid && kill $$" EXIT
```
(Now the client supports the `forward` command.)

## Proxy

### Fabric8 Kubernetes Client

https://github.com/fabric8io/kubernetes-client


### Kubernetes API GRPC client load balancing

https://github.com/saturnism/grpc-java-by-example


Port-forward the server in k8s:
`kubectl port-forward $(kubectl get pods -l app=floo-server -o=jsonpath='{.items[0].metadata.name}') 6565`

Run the proxy in k8s:
`kubectl run floo-proxy --image=gcr.io/talknerdy-one/github-chickenbane-floo-proxy:latest`
