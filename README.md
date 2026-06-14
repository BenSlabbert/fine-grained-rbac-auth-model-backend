## Podman

Run the podman service

```shell
mkdir -p /run/user/$(id -u)/podman
chmod 777 -R /run/user/$(id -u)/podman
podman system service --time=0 unix:///run/user/$(id -u)/podman/podman.sock
```

## Example API call path

user → gateway → backend app → iam (check user permissions/scope)

### gateway
* stateful service
* keeps browser session and manages cookies
* generates JWT tokens for backend calls to downstream services

### iam
* stateless service
* provides an API for services to check permissions/scopes

### JWT tokens are:
* stateless service
* short-lived - for single API calls
* small - very minimal information to identify the caller
* do not contain any permissions
    * services must call the iam service to resolve permissions and scopes
