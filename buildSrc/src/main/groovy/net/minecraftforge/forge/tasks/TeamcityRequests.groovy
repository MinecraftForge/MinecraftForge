package net.minecraftforge.forge.tasks

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import groovy.transform.CompileStatic
import org.eclipse.jgit.api.Git

import javax.annotation.Nullable

@CompileStatic
class TeamcityRequests {
    public static final Gson GSON = new Gson()

    static <T> T jsonRequest(TypeToken<T> clazz, String url) throws Exception {
        final URLConnection conn = URI.create(url).toURL().openConnection()
        conn.setRequestProperty('Accept', 'application/json')
        try (final InputStream is = conn.getInputStream()) {
            GSON.fromJson(new InputStreamReader(is), clazz.getType())
        }
    }

    @Nullable
    static Build findMatching(String commitId) throws IOException {
        final builds = jsonRequest(new TypeToken<Builds>() {},
                "https://teamcity.minecraftforge.net/guestAuth/app/rest/builds?locator=revision:($commitId),buildType:(id:MinecraftForge_MinecraftForge_MinecraftForge_MinecraftForge__Build)")
        builds.build.find()
    }

    @Nullable
    static String attemptFindBase(File gitPath) {
        try (final git = Git.open(gitPath)) {
            for (final commit in git.log().setMaxCount(100).call()) {
                // Find the first commit which was built on CI
                final build = findMatching(commit.id.name)
                if (build) {
                    return build.number
                }
            }
        } catch (Exception ignored) {}
        return null
    }

    static final class Builds {
        public int count
        public String href
        public List<Build> build
    }

    static final class Build {
        public int id
        public String number
        public String buildTypeId
    }
}