# The Merchant Problem

The Merchant Problem is an optimization problem similar to the travelling
salesman. A merchant must travel from port to port, buying and selling
commodities, and attempt to make the largest profit possible.

## Commodities

Commodities are bought and sold at ports by the [merchant](#merchant). The
commodity "Gold" is the most versatile commodity. It is what other commodities
are bought and sold with. It also determines how much profit a
merchant has made. Each commodity has a weight associated with it. The weight
is per unit. Ports can carry an infinite (read: 2^32) amount of commodities.
Merchants can only hold as much as their ship will allow.

## Ports

The merchant travels from port to port, trade commodities. Each port has
a certain set of commodities they are willing to trade. For more detail, go to
[Port Supplies](#port-supplies). Ports are also connected to other ports
through [Routes](#routes). The merchant travels along routes to get from port
to port. However, not all ports have routes to all others.

## Port Supplies

The commodities a port is willing to buy and sell is listed in the PortSupply
table. Each supply has an amount on hand (how much the port owns), along with
a buying price (for buying from the port) and a sell price (for selling to
the port).

## Routes

The merchant travels from port to port using routes. Not all ports have routes
to all other ports. Using a route may incur a cost to the merchant, and the
cost may not be the same each way. See [Route Costs](#route-costs) below.

## Route Costs

The commodities consumed during a trip along a route are listed in the
RouteCost table, along with how much of the commodity is required. There may
be several costs per route, but the costs may not be the same each direction.

## Merchant

The merchant travels from port to port, buying and selling commodities along
the way. He begins at a home port and must end at the same home port. His goal
is to return home with the largest profit possible. This profit is measured in
the commodity "Gold". When he returns to his home port, he may choose to
off-load all his remaining supplies in return for their Gold value. Merchants
can only carry as many commodities as the ship can hold, determined by the
commodity weight and ship capacity.

## Merchant Supplies

The merchant can hold every commodity, but not in excess of the sum of their
respective weights. A merchant's supplies are sold and refilled at ports.

## Voyages

A merchant's voyage is a log of the ports he visits.

## Transactions

Everytime the merchant buys or sells a commodity, it's recorded as a
transaction and tied to the relevant log in his Voyage.