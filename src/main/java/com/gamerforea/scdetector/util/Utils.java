package com.gamerforea.scdetector.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;

import com.gamerforea.scdetector.similar.ClassContainer;
import com.gamerforea.scdetector.similar.FieldOrMethod;
import com.google.common.io.ByteStreams;

public class Utils
{
	public static ClassContainer getClassContainer(ClassNode clazz)
	{
		final ClassContainer container = new ClassContainer(clazz.name, clazz.superName);
		clazz.interfaces.stream().forEach((i) -> container.interfaces.add(i));
		clazz.fields.stream().forEach((f) ->
		{
			container.fields.add(new FieldOrMethod(f.name, f.desc));
			if (f.value instanceof String) container.strings.add(f.value.toString());
		});
		clazz.methods.stream().forEach((m) ->
		{
			container.methods.add(new FieldOrMethod(m.name, m.desc));
			Arrays.stream(m.instructions.toArray()).filter((i) -> i instanceof LdcInsnNode && ((LdcInsnNode) i).cst instanceof String && !((LdcInsnNode) i).cst.toString().isEmpty()).forEach((i1) -> container.strings.add(((LdcInsnNode) i1).cst.toString()));
		});
		return container;
	}

	public static List<ClassContainer> getClassContainers(List<ClassNode> classes)
	{
		final List<ClassContainer> constants = new ArrayList<>(classes.size());
		classes.parallelStream().forEach((clazz) ->
		{
			synchronized (constants)
			{
				constants.add(getClassContainer(clazz));
			}
		});
		return constants.parallelStream().sorted((o1, o2) -> sort(o1.interfaces, o2.interfaces)).sorted((o1, o2) -> sort(o1.fields, o2.fields)).sorted((o1, o2) -> sort(o1.methods, o2.methods)).sorted((o1, o2) -> sort(o1.strings, o2.strings)).collect(Collectors.toList());
	}

	public static List<ClassNode> getClasses(String path) throws IOException
	{
		ZipFile zip = new ZipFile(path);
		Object[] entries = zip.stream().filter((e) -> !e.isDirectory() && e.getName().endsWith(".class")).toArray();
		List<byte[]> bytes = new ArrayList<>(entries.length);
		for (Object entry : entries)
			bytes.add(ByteStreams.toByteArray(zip.getInputStream((ZipEntry) entry)));
		zip.close();
		List<ClassNode> classes = new ArrayList<>(bytes.size());
		bytes.parallelStream().forEach((e) ->
		{
			ClassNode node = new ClassNode();
			new ClassReader(e).accept(node, 0);
			synchronized (classes)
			{
				classes.add(node);
			}
		});
		return classes;
	}

	public static int sort(List<?> list1, List<?> list2)
	{
		return list2.size() - list1.size();
	}
}