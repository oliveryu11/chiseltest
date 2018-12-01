// See LICENSE for license details.

package chisel3.tester

import chisel3.experimental.MultiIOModule

import scala.util.DynamicVariable

object Context {
  class Instance(val backend: BackendInterface, val env: TestEnvInterface) {
  }

  private var context = new DynamicVariable[Option[Instance]](None)

  def run[T <: MultiIOModule](backend: BackendInstance[T], env: TestEnvInterface, testFn: T => Unit) {
    context.withValue(Some(new Instance(backend, env))) {
      backend.run(testFn)
    }
  }

  // TODO: better integration points for default tester selection
  def createDefaultTester[T <: MultiIOModule](dutGen: () => T): BackendInstance[T] = {
    TreadleExecutive.start(dutGen)
  }

  // TODO: add TesterOptions (from chisel-testers) and use that to control default tester selection.
  def createDefaultTester[T <: MultiIOModule](
    dutGen: () => T,
    options: Testers2OptionsManager
  ): BackendInstance[T] = {

    TreadleExecutive.start(dutGen, Some(options))
  }

  def apply(): Instance = context.value.get
}
