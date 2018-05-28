
### Kubernetes API GRPC client load balancing

https://github.com/saturnism/grpc-java-by-example

### Spring Shell

Josh Long https://www.youtube.com/watch?v=h6nMjjxJWjk

Running the client in k8s:
`kubectl run -i -t floo-client --image=gcr.io/talknerdy-one/github-chickenbane-floo-client:latest --restart=Never --rm`

Port-forward the server in k8s:
`kubectl port-forward $(kubectl get pods -l app=floo-server -o=jsonpath='{.items[0].metadata.name}') 6565`

