# Ostara Agent

This is the repository for the [Ostara](https://github.com/krud-dev/ostara) Agent.

An agent is a worker installed in your environment. It plays a pivotal role in connecting Ostara to the services within your environment. By installing an agent, you enable Ostara to discover and monitor your applications automatically.

## Installing agents

Find out how to install agents on -

* [Kubernetes](https://docs.ostara.dev/documentation/agents/installation/kubernetes)
* [Mac/Linux](https://docs.ostara.dev/documentation/agents/installation/mac-linux#installation-using-brew)
* [Standalone](https://docs.ostara.dev/documentation/agents/installation/mac-linux#standalone-installation)

## Purpose of agents
The agent acts as a bridge between Ostara and the services running within your environment. It facilitates communication with actuator, and most importantly it runs service discovery in the environment. Once services are discovered, it adds them to your Ostara app and then starts retrieving metrics and data from those services, and relaying it back to Ostara for analysis and monitoring.

## Useful links

- [Homepage](https://ostara.dev/)
- [Main Repository](https://github.com/krud-dev/ostara)
- [Documentation](https://docs.ostara.dev/)
