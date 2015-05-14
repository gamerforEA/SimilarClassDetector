package com.gamerforea.scdetector;

import static com.gamerforea.scdetector.util.Utils.getClassContainers;
import static com.gamerforea.scdetector.util.Utils.getClasses;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gamerforea.scdetector.similar.ClassContainer;
import com.gamerforea.scdetector.similar.SimilarContainer;
import com.gamerforea.scdetector.similar.SimilarContainer.GsonSimilarContainer;
import com.gamerforea.scdetector.util.SimilarUtils;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainCLI
{
	/**
	 * Input_1 Input_2 Output StringsSimilar FieldsSimilar MethodsSimilar
	 * test/1.8.jar test/1.8.4.jar similars.json 0.9 0.9 0.9
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		String input1 = args[0];
		String input2 = args[1];
		String output = args[2];
		float stringsSimilar = Float.valueOf(args[3]);
		float fieldsSimilar = Float.valueOf(args[4]);
		float methodsSimilar = Float.valueOf(args[5]);

		List<ClassContainer> containers1 = getClassContainers(getClasses(input1));
		List<ClassContainer> containers2 = getClassContainers(getClasses(input2));
		List<SimilarContainer> similars = SimilarUtils.getSimilars(containers1, containers2);
		similars = SimilarUtils.diffStrings(similars, stringsSimilar);
		similars = SimilarUtils.diffFieldsCount(similars, fieldsSimilar);
		similars = SimilarUtils.diffMethodsCount(similars, methodsSimilar);
		List<GsonSimilarContainer> list = new ArrayList<>();
		similars.parallelStream().forEach((s) ->
		{
			synchronized (list)
			{
				list.add(s.toGson());
			}
		});

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Files.write(gson.toJson(list.stream().filter((s) -> !s.similars.isEmpty()).collect(Collectors.toList())).getBytes(), new File(output));
	}
}