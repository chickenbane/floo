# Integrations with Spring Shell, GRPC, and Kubernetes

## Client

### Spring Shell
Josh Long https://www.youtube.com/watch?v=h6nMjjxJWjk


### Official Kubernetes Client

https://github.com/kubernetes-client/java


### Running and port-forwarding the proxy

Build, port-forward the proxy, and run the client: `./floo`
(Spring Boot's `bootRun` + Spring Shell play oddly together)


## Proxy

### Fabric8 Kubernetes Client

https://github.com/fabric8io/kubernetes-client


### Kubernetes API GRPC client load balancing

https://github.com/saturnism/grpc-java-by-example


Port-forward the server in k8s:
`kubectl port-forward $(kubectl get pods -l app=floo-server -o=jsonpath='{.items[0].metadata.name}') 6565`

Used GCP to deploy the floo-proxy container image.

## Server

Used GCP to deploy the floo-server container image, then used Actions->Expose to create a service w/type=ClusterIP.