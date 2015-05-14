package com.gamerforea.scdetector.util;

import java.util.ArrayList;
import java.util.List;

import com.gamerforea.scdetector.similar.ClassContainer;
import com.gamerforea.scdetector.similar.SimilarContainer;
import com.gamerforea.scdetector.similar.SimilarData;

public class SimilarUtils
{
	public static List<SimilarContainer> diffMethodsCount(List<SimilarContainer> similars, float minSimilar)
	{
		List<SimilarContainer> newSimilars = new ArrayList<>();
		similars.parallelStream().filter((container) -> !container.classContainer.methods.isEmpty()).forEach((container) ->
		{
			List<SimilarData> list = new ArrayList<>();
			container.similarClasses.stream().filter((data) -> !data.classContainer.methods.isEmpty()).forEach((data) ->
			{
				int size1 = container.classContainer.methods.size();
				int size2 = data.classContainer.methods.size();
				float similar = (float) Math.min(size1, size2) / Math.max(size1, size2);
				if (similar >= minSimilar)
				{
					data.similarMethods = similar;
					list.add(data);
				}
			});
			container.similarClasses = list;
			synchronized (newSimilars)
			{
				newSimilars.add(container);
			}
		});
		return newSimilars;
	}

	public static List<SimilarContainer> diffFieldsCount(List<SimilarContainer> similars, float minSimilar)
	{
		List<SimilarContainer> newSimilars = new ArrayList<>();
		similars.parallelStream().filter((container) -> !container.classContainer.fields.isEmpty()).forEach((container) ->
		{
			List<SimilarData> list = new ArrayList<>();
			container.similarClasses.stream().filter((data) -> !data.classContainer.fields.isEmpty()).forEach((data) ->
			{
				int size1 = container.classContainer.fields.size();
				int size2 = data.classContainer.fields.size();
				float similar = (float) Math.min(size1, size2) / Math.max(size1, size2);
				if (similar >= minSimilar)
				{
					data.similarFields = similar;
					list.add(data);
				}
			});
			container.similarClasses = list;
			synchronized (newSimilars)
			{
				newSimilars.add(container);
			}
		});
		return newSimilars;
	}

	public static List<SimilarContainer> diffStrings(List<SimilarContainer> similars, float minSimilar)
	{
		List<SimilarContainer> newSimilars = new ArrayList<>();
		similars.parallelStream().filter((container) -> !container.classContainer.strings.isEmpty()).forEach((container) ->
		{
			List<SimilarData> list = new ArrayList<>();
			container.similarClasses.stream().filter((data) -> !data.classContainer.strings.isEmpty()).forEach((data) ->
			{
				Int i = new Int();
				container.classContainer.strings.stream().forEach((s1) ->
				{
					data.classContainer.strings.stream().forEach((s2) ->
					{
						if (!i.updated && s1.equals(s2))
						{
							i.i++;
							i.updated = true;
						}
					});
					i.updated = false;
				});
				float similar = (float) i.i / container.classContainer.strings.size();
				if (similar >= minSimilar)
				{
					data.similarStrings = similar;
					list.add(data);
				}
			});
			container.similarClasses = list;
			synchronized (newSimilars)
			{
				newSimilars.add(container);
			}
		});
		return newSimilars;
	}

	public static List<SimilarContainer> getSimilars(List<ClassContainer> c1, List<ClassContainer> c2)
	{
		List<SimilarContainer> similars = new ArrayList<>();
		c1.parallelStream().forEach((clazz1) ->
		{
			SimilarContainer similar = new SimilarContainer(clazz1);
			c2.stream().filter((clazz2) -> clazz1.interfaces.size() == clazz2.interfaces.size()).forEach((clazz2) ->
			{
				similar.addSimilarClass(clazz2);
			});
			synchronized (similars)
			{
				similars.add(similar);
			}
		});
		return similars;
	}
}