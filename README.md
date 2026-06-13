## Podman

Run the podman service

```shell
mkdir -p /run/user/$(id -u)/podman
chmod 777 -R /run/user/$(id -u)/podman
podman system service --time=0 unix:///run/user/$(id -u)/podman/podman.sock
```
