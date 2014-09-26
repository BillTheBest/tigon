# TwitterAnalytics

## Overview
An application collects Tweets and logs the top 10 hashtags used in the last minute.

## Twitter Configuration
In order to utilize the ``TweetCollector`` flowlet, which pulls a small sample stream via the Twitter API, an API key and Access token must be configured.
Follow the steps at [Twitter oauth access tokens](https://dev.twitter.com/oauth/overview/application-owner-access-tokens) to obtain these credentials.
These configurations must be provided as runtime arguments to the Flow prior to starting it in order to use the ``TweetCollector`` flowlet.

## Flow Runtime Arguments
When starting the Application from the command line, runtime arguments may need to be specified.

The required Twitter authorization properties ("oauth-properties") include all of these:

"oauth.consumerKey" - See ```Twitter Configuration``` above.

"oauth.consumerSecret" - See ```Twitter Configuration``` above.

"oauth.Token" - See ```Twitter Configuration``` above.

"oauth.TokenSecret" - See ```Twitter Configuration``` above.


## Installation

Build the Application jar:
```
MAVEN_OPTS="-Xmx512m" mvn package -DskipTests -pl tigon-examples -am -amd -P examples
```

To deploy the Application to a standalone instance of Tigon:
```
$ ./run_standalone.sh /path/to/TwitterAnalytics-0.1.0.jar co.cask.tigon.analytics.TwitterAnalytics [ oauth-properties ]
```

To deploy the Application to a distributed instance of Tigon:
```
$ ./run_distributed.sh <ZookeeperQuorum> <HDFSNamespace>
> START /path/to/TwitterAnalytics-0.1.0.jar co.cask.tigon.analytics.TwitterAnalytics [ oauth-properties ]
```

The top ten hashtags used in the previous minute get recorded. In the case of standalone instance of Tigon,
the results will appear immediately in the Tigon command line interface; in the case of distributed instance of Tigon,
the results will be written to the logs of the YARN container of the Flowlet.

## License and Trademarks

Copyright © 2014 Cask Data, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
