package com._0d0a

import java.util.concurrent.ExecutionException

import co.paralleluniverse.strands.SuspendableCallable
import co.paralleluniverse.strands.SuspendableRunnable
import co.paralleluniverse.strands.channels.Channels
import co.paralleluniverse.strands.channels.IntChannel

import co.paralleluniverse.fibers.Fiber
import co.paralleluniverse.fibers.SuspendExecution
import co.paralleluniverse.fibers.Suspendable
import co.paralleluniverse.kotlin.fiber
import kotlin.platform.platformStatic

/**
 * Increasing-Echo Quasar Example
 *
 * @author circlespainter
 */
public class QuasarIncreasingEchoApp {
    companion object{
         fun doAll():Int  {
            val increasingToEcho = Channels.newIntChannel(0) // Synchronizing channel (buffer = 0)
            val echoToIncreasing = Channels.newIntChannel(0) // Synchronizing channel (buffer = 0)

            val increasing = fiber("INCREASER", @Suspendable{
                ////// The following is enough to test instrumentation of synchronizing methods
                // synchronized(new Object()) {}

                var curr = 0
                for (i in 0..9)  {
                    Fiber.sleep(1000)
                    System.out.println("INCREASER sending: " + curr)
                    increasingToEcho.send(curr)
                    curr = echoToIncreasing.receive()
                    System.out.println("INCREASER received: " + curr)
                    curr++
                    System.out.println("INCREASER now: " + curr)
                }
                System.out.println("INCREASER closing channel and exiting")
                increasingToEcho.close()
                curr
            });

            val echo = fiber("ECHO", @Suspendable{
                var curr:Int?
                while (true) {
                    Fiber.sleep(1000)
                    curr = increasingToEcho.receive()
                    System.out.println("ECHO received: " + curr)

                    if (curr != null) {
                        System.out.println("ECHO sending: " + curr)
                        echoToIncreasing.send(curr)
                    } else {
                        System.out.println("ECHO detected closed channel, closing and exiting")
                        echoToIncreasing.close()
                        break
                    }
                }
            })

            try {
                increasing.join()
                echo.join()
            } catch (e:ExecutionException) {
                e.printStackTrace()
            } catch (e:InterruptedException) {
                e.printStackTrace()
            }

            return increasing.get()
        }
    }
}

fun main(args : Array<String>) {
    System.setProperty("co.paralleluniverse.fibers.disableAgentWarning", "true")
    QuasarIncreasingEchoApp.doAll()
}