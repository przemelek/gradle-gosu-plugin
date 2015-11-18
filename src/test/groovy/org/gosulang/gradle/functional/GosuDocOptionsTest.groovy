package org.gosulang.gradle.functional

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure

class GosuDocOptionsTest extends AbstractGosuPluginSpecification {

    File srcMainGosu
    File simplePogo

    /**
     * super#setup is invoked automatically
     * @return
     */
    @Override
    def setup() {
        srcMainGosu = testProjectDir.newFolder('src', 'main', 'gosu')
    }

    def 'execute gosudoc with default options'() {
        given:
        buildScript << getBasicBuildScriptForTesting()

        simplePogo = new File(srcMainGosu, asPath('example', 'gradle', 'SimplePogo.gs'))
        simplePogo.getParentFile().mkdirs()
        simplePogo << """
        package example.gradle
        
        /**
         * I can has gosudoc
         */
        class SimplePogo {
          
          function doIt(intArg : int) : String {
            var x = intArg
            return x as String
          }
        }"""

        when:
        GradleRunner runner = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath(pluginClasspath)
                .withArguments('gosudoc', '-is')

        BuildResult result = runner.build()

        then:
        notThrown(UnexpectedBuildFailure)
        result.standardOutput.contains('Generating Documentation')
        result.standardOutput.contains('example.gradle.SimplePogo - document : true')
        
        File gosudocOutputRoot = new File(testProjectDir.root, asPath('build', 'docs', 'gosudoc'))
        File simplePogoGosudoc = new File(gosudocOutputRoot, asPath('example', 'gradle', 'example.gradle.SimplePogo.html'))

        //validate the generated HTML
        simplePogoGosudoc.exists()
        simplePogoGosudoc.readLines().contains('<div class="block">I can has gosudoc</div>')
        
    }
    
    
}
