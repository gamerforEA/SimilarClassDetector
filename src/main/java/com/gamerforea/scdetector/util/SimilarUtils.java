package com.gamerforea.scdetector.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gamerforea.scdetector.similar.ClassContainer;
import com.gamerforea.scdetector.similar.SimilarContainer;
import com.gamerforea.scdetector.similar.SimilarData;

public class SimilarUtils
{
	public static final String[] forceFilter = new String[] { "java/", "com/google/", "paulscode/", "io/netty/" };

	public static List<SimilarContainer> validateSimilars(List<SimilarContainer> similars)
	{
		similars.parallelStream().filter((container) -> container.similarClasses.size() == 1).forEach((container) -> container.validated = true);
		return similars;
	}

	public static List<SimilarContainer> diffMethodsCount(List<SimilarContainer> similars, float minSimilar)
	{
		similars.parallelStream().filter((container) -> !container.classContainer.methods.isEmpty()).forEach((container) ->
		{
			List<SimilarData> list = new ArrayList<>();
			container.similarClasses.stream().filter((data) -> !data.classContainer.methods.isEmpty()).forEach((data) ->
			{
				int size1 = container.classContainer.methods.size();
				int size2 = data.classContainer.methods.size();
				float similar = (float) Math.min(size1, size2) / Math.max(size1, size2);
				if (similar >= minSimilar) list.add(data);
			});
			container.similarClasses = list;
		});
		similars.parallelStream().filter((container) -> container.classContainer.methods.isEmpty()).forEach((container) ->
		{
			container.similarClasses = new ArrayList<>(container.similarClasses.stream().filter((data) -> data.classContainer.methods.isEmpty()).collect(Collectors.toList()));
		});
		return similars;
	}

	public static List<SimilarContainer> diffFieldsCount(List<SimilarContainer> similars, float minSimilar)
	{
		similars.parallelStream().filter((container) -> !container.classContainer.fields.isEmpty()).forEach((container) ->
		{
			List<SimilarData> list = new ArrayList<>();
			container.similarClasses.stream().filter((data) -> !data.classContainer.fields.isEmpty()).forEach((data) ->
			{
				int size1 = container.classContainer.fields.size();
				int size2 = data.classContainer.fields.size();
				float similar = (float) Math.min(size1, size2) / Math.max(size1, size2);
				if (similar >= minSimilar) list.add(data);
			});
			container.similarClasses = list;
		});
		similars.parallelStream().filter((container) -> container.classContainer.fields.isEmpty()).forEach((container) ->
		{
			container.similarClasses = new ArrayList<>(container.similarClasses.stream().filter((data) -> data.classContainer.fields.isEmpty()).collect(Collectors.toList()));
		});
		return similars;
	}

	public static List<SimilarContainer> diffStrings(List<SimilarContainer> similars, float minSimilar)
	{
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
				if (similar >= minSimilar) list.add(data);
			});
			container.similarClasses = list;
		});
		similars.parallelStream().filter((container) -> container.classContainer.strings.isEmpty()).forEach((container) ->
		{
			container.similarClasses = new ArrayList<>(container.similarClasses.stream().filter((data) -> data.classContainer.strings.isEmpty()).collect(Collectors.toList()));
		});
		return similars;
	}

	public static List<SimilarContainer> getSimilars(List<ClassContainer> c1, List<ClassContainer> c2)
	{
		List<SimilarContainer> similars = new ArrayList<>();
		c1.parallelStream().forEach((clazz1) ->
		{
			SimilarContainer container = new SimilarContainer(clazz1);
			c2.stream().filter((clazz2) -> clazz1.interfaces.size() == clazz2.interfaces.size() && clazz1.type == clazz2.type).forEach((clazz2) ->
			{
				boolean add = true;
				if (clazz1.interfaces.size() == 1)
				{
					for (String packet : forceFilter)
					{
						if (clazz1.interfaces.get(0).startsWith(packet) || clazz2.interfaces.get(0).startsWith(packet))
						{
							add = clazz1.interfaces.get(0).equals(clazz2.interfaces.get(0));
							break;
						}
					}
				}
				if (add)
				{
					for (String packet : forceFilter)
					{
						if (clazz1.superName.startsWith(packet) || clazz2.superName.startsWith(packet))
						{
							add = clazz1.superName.equals(clazz2.superName);
							break;
						}
					}
				}
				if (add) container.addSimilarClass(clazz2);
			});
			synchronized (similars)
			{
				similars.add(container);
			}
		});
		return similars;
	}
}