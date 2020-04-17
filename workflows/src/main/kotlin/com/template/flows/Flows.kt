package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.github.andrewoma.dexx.kollection.immutableMapOf
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator(private val counterParty: Party) : FlowLogic<Unit>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call() {
        val session = initiateFlow(counterParty)
        // Instantiate an immutable map as a stack variable before suspending the flow
        val map = immutableMapOf("foo" to "bar")
        val response = session.sendAndReceive<String>("ping")
        response.unwrap { data -> println("$data, $map") }
    }
}

@InitiatedBy(Initiator::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val request = counterpartySession.receive<String>()
        counterpartySession.send("pong")
    }
}
