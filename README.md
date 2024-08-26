# Содержание
[[_TOC_]]

# profile-server

# Description

# Links
|Tool| Link                                                                                   |
|:---|:---------------------------------------------------------------------------------------|
|Build|[Jenkins](https://jenkins.digitalchief.tech/job/cms/job/back/job/cms%252Fback%252Fengine/)|
|Pipe|[pipe_cms_java_maven_tomcat_engine.groovy](https://gitlab.digitalchief.tech/devops-public/shared-library/-/blob/master/vars/pipe_cms_java_maven_tomcat_engine.groovy)|
|Vault|[Stage](https://vault.digitalchief.tech/ui/vault/secrets/cms/list/stage/back/engine/)|

# Параметры запуска
## ENV
### Main
|ENV Var Name|Stage| Description|
|:-----------|:------|:------------------------|
|`SERVER_PORT`|`8080`| TCP port app listens to.|
|`PROFILE_MANAGEMENT_TOKEN`|`defaultManagementToken`|Default management token for get statistic.|
|`MONGODB_HOST`|`localhost`|Database host|
|`MONGODB_PORT`|`27017`|Database TCP port listens to.|
|`MONGODB_SCHEMA`|`profile`|Database collection name|
|`MONGODB_USER`||Database username|
|`MONGODB_PASSWORD`||Database password|
|`MONGODB_INIT_ON_RUN`|`true`|Init mongodb collections on start service|
|`MAIL_HOST`|`test-cms-BRANCH_NAME.int.digitalchief.tech`|Mail server host.|
|`MAIL_PORT`|1025|Mail server post.|
|`MAIL_USERNAME`||Mail username.|
|`MAIL_PASSWORD`||Mail password.|
|`MAIL_AUTH_ENABLED`|true|Mail server authentication enabled.|
|`MAIL_TLS_ENABLED`|false|Mail server TLS enabled.|


## Paths
| Path                                    | Type | Description|
|:----------------------------------------|:-----|:------------------------|
|`/api/1/target/create`                   |`POST`|Create target to deploy site.|
|`/api/1/target/delete/default/{site-name}`|`POST`|Remove target for site deployment.|