/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.integtests

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.RepoScriptBlockUtil
import org.gradle.integtests.fixtures.Sample
import org.gradle.integtests.fixtures.archives.TestReproducibleArchives
import org.gradle.test.fixtures.file.TestFile
import org.gradle.util.GradleVersion
import org.junit.Rule

import java.util.jar.Manifest

@TestReproducibleArchives
class OsgiProjectSampleIntegrationTest extends AbstractIntegrationSpec {

    @Rule public final Sample sample = new Sample(testDirectoryProvider, 'osgi')

    def setup() {
        executer.usingInitScript(RepoScriptBlockUtil.createMirrorInitScript())
    }

    def "OSGi project samples"() {
        TestFile osgiProjectDir = sample.dir
        executer.inDirectory(osgiProjectDir).withTasks('clean', 'assemble').run()
        TestFile tmpDir = testDirectory
        osgiProjectDir.file('build/libs/osgi-1.0.jar').unzipTo(tmpDir)
        Manifest manifest
        tmpDir.file('META-INF/MANIFEST.MF').withInputStream { InputStream instr ->
            manifest = new Manifest(instr)
        }

        expect:
        manifest != null
        manifest.mainAttributes.getValue('Bundle-Name') == 'Example Gradle Activator'
        manifest.mainAttributes.getValue('Bundle-ManifestVersion') == '2'
        manifest.mainAttributes.getValue('Tool') == 'Bnd-3.4.0.201707252008'
        manifest.mainAttributes.getValue('Bundle-Version') == '1.0.0'
        manifest.mainAttributes.getValue('Bundle-SymbolicName') == 'gradle_tooling.osgi'
        manifest.mainAttributes.getValue('Built-By') ==  GradleVersion.current().version
    }
}
